provider "aws" {
  region = "us-east-1"
}

resource "aws_ecr_repository" "springboot_ecr" {
  name = "springboot-app"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "springboot-ecr"
  }
}
