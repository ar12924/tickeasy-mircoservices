#!/usr/bin/env bash
set -euo pipefail

REGION="${REGION:-ap-northeast-1}"

# Install Docker & docker-compose
sudo yum update -y
sudo amazon-linux-extras enable docker
sudo yum install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user || true

sudo curl -L "https://github.com/docker/compose/releases/download/2.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install AWS CLI v2
if ! command -v aws >/dev/null 2>&1; then
  curl "https://awscli.amazonaws.com/awscli-exe-linux-$(uname -m).zip" -o "awscliv2.zip"
  unzip -q awscliv2.zip
  sudo ./aws/install
fi

# Fetch parameters from SSM Parameter Store (adjust names)
DB_URL=$(aws ssm get-parameter --region "$REGION" --name "/tickeasy/db/url" --with-decryption --query Parameter.Value --output text || true)
DB_USER=$(aws ssm get-parameter --region "$REGION" --name "/tickeasy/db/user" --with-decryption --query Parameter.Value --output text || true)
DB_PASS=$(aws ssm get-parameter --region "$REGION" --name "/tickeasy/db/pass" --with-decryption --query Parameter.Value --output text || true)
JWT_SECRET=$(aws ssm get-parameter --region "$REGION" --name "/tickeasy/jwt/secret" --with-decryption --query Parameter.Value --output text || true)
MAIL_USER=$(aws ssm get-parameter --region "$REGION" --name "/tickeasy/mail/user" --with-decryption --query Parameter.Value --output text || true)
MAIL_PASS=$(aws ssm get-parameter --region "$REGION" --name "/tickeasy/mail/pass" --with-decryption --query Parameter.Value --output text || true)
S3_BUCKET=$(aws ssm get-parameter --region "$REGION" --name "/tickeasy/s3/bucket" --with-decryption --query Parameter.Value --output text || true)

cat > .env <<EOF
JWT_SECRET=${JWT_SECRET}
SPRING_MAIL_USERNAME=${MAIL_USER}
SPRING_MAIL_PASSWORD=${MAIL_PASS}
S3_BUCKET=${S3_BUCKET}
EOF

# Pull images from your registry (replace repo)
docker pull <replace-with-your-registry>/member:latest
docker pull <replace-with-your-registry>/gateway:latest

docker-compose up -d

echo "Deployment started. Use 'docker ps' to verify containers."


