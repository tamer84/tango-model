# ========================================
# Initialization
# ========================================
terraform {
  // Declares where terraform stores the application state
  backend "s3" {
    encrypt = "true"
    bucket  = "tango-terraform"
    // terraform does not support variables on the init, you need to put the app name below
    key            = "resources/tango-model/tfstate.tf"
    region         = "eu-central-1"
    dynamodb_table = "terraform"
  }
}

provider "aws" {
  // Use the AWS provider from terraform https://www.terraform.io/docs/providers/aws/index.html
  region = "eu-central-1"
}

provider "github" {
  token        = data.terraform_remote_state.account_resources.outputs.github_access_token
  base_url     = "https://github.com/tamer84"
}

data "terraform_remote_state" "account_resources" {
  // Imports the account resources to use the shared information
  backend = "s3"
  config = {
    encrypt = "true"
    bucket  = "tango-terraform"
    key     = "account_resources/tfstate.tf"
    region  = "eu-central-1"
  }
  workspace = "default"
}

data "terraform_remote_state" "environment_resources" {
  // Imports the environment specific resources to use the shared information
  backend = "s3"
  config = {
    encrypt = "true"
    bucket  = "tango-terraform"
    key     = "environment_resources/tfstate.tf"
    region  = "eu-central-1"
  }
  workspace = terraform.workspace
}

data "terraform_remote_state" "terraform_build_image_resources" {
  backend = "s3"
  config = {
    encrypt = "true"
    bucket  = "tango-terraform"
    key     = "resources/terraform-build-image/tfstate.tf"
    region  = "eu-central-1"
  }
  workspace = terraform.workspace
}

data "aws_caller_identity" "current" {}

# ========================================
# Locals
# ========================================
locals {
  // Change the CICD branch here depending on the terraform workspace, if needed
  cicd_branch = contains(["dev", "test", "int"], terraform.workspace) ? "develop" : "main"
}

# ========================================
# CICD
# ========================================
module "cicd" {
  source = "git::ssh://git@github.com/tamer84/infra.git//modules/cicd?ref=develop"

  codestar_connection_arn = data.terraform_remote_state.account_resources.outputs.git_codestar_conn.arn

  pipeline_base_configs = {
    "name"        = "tango-model-${terraform.workspace}",
    "bucket_name" = data.terraform_remote_state.environment_resources.outputs.cicd_bucket.id,
    "role_arn"    = data.terraform_remote_state.account_resources.outputs.cicd_role.arn,
  }

  codebuild_build_stage = {
    "project_name"        = "tango-model-${terraform.workspace}",
    "github_branch"       = local.cicd_branch,
    "github_repo"         = "tamer84/tango-model",
    "github_access_token" = data.terraform_remote_state.account_resources.outputs.github_access_token,
    "github_certificate"  = "${data.terraform_remote_state.environment_resources.outputs.cicd_bucket.arn}/${data.terraform_remote_state.environment_resources.outputs.github_cert.id}",

    "service_role_arn"   = data.terraform_remote_state.account_resources.outputs.cicd_role.arn,
    "cicd_bucket_id"     = data.terraform_remote_state.environment_resources.outputs.cicd_bucket.id,
    "vpc_id"             = data.terraform_remote_state.environment_resources.outputs.vpc.id
    "subnets_ids"        = data.terraform_remote_state.environment_resources.outputs.private-subnet.*.id
    "security_group_ids" = [data.terraform_remote_state.environment_resources.outputs.group_internal_access.id]

    "docker_img_url"                   = data.terraform_remote_state.terraform_build_image_resources.outputs.ecr_repository.repository_url,
    "docker_img_tag"                   = "latest",
    "docker_img_pull_credentials_type" = "SERVICE_ROLE",
    "buildspec"                        = "./buildspec.yml",
    "env_vars" = [
      {
        name  = "ENVIRONMENT"
        value = terraform.workspace
      }
    ]
  }
}
