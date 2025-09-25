#!/bin/bash

# JWT Secret Key Generator
# This script generates a secure random JWT secret key

echo "🔐 JWT Secret Key Generator"
echo "=========================="
echo ""

# Method 1: Using OpenSSL (Recommended)
if command -v openssl &> /dev/null; then
    echo "Method 1: OpenSSL (Recommended)"
    JWT_SECRET_OPENSSL=$(openssl rand -base64 64 | tr -d '\n')
    echo "JWT_SECRET=\"$JWT_SECRET_OPENSSL\""
    echo ""
fi

# Method 2: Using /dev/urandom (Linux/Mac)
if [[ -c /dev/urandom ]]; then
    echo "Method 2: /dev/urandom"
    JWT_SECRET_URANDOM=$(head -c 64 /dev/urandom | base64 | tr -d '\n')
    echo "JWT_SECRET=\"$JWT_SECRET_URANDOM\""
    echo ""
fi

# Method 3: Using Node.js (if available)
if command -v node &> /dev/null; then
    echo "Method 3: Node.js crypto"
    JWT_SECRET_NODE=$(node -e "console.log(require('crypto').randomBytes(64).toString('base64'))")
    echo "JWT_SECRET=\"$JWT_SECRET_NODE\""
    echo ""
fi

# Method 4: Using Python (if available)
if command -v python3 &> /dev/null; then
    echo "Method 4: Python secrets"
    JWT_SECRET_PYTHON=$(python3 -c "import secrets; print(secrets.token_urlsafe(64))")
    echo "JWT_SECRET=\"$JWT_SECRET_PYTHON\""
    echo ""
fi

echo "⚠️  IMPORTANT SECURITY NOTES:"
echo "   1. Never commit the generated key to version control"
echo "   2. Use different keys for development, staging, and production"
echo "   3. Store keys securely (AWS Secrets Manager, Azure Key Vault, etc.)"
echo "   4. Rotate keys regularly (every 6-12 months)"
echo "   5. Keep backup of the key in secure location"
echo ""

echo "📋 To use the generated key:"
echo "   1. Copy one of the generated keys above"
echo "   2. Set it as JWT_SECRET environment variable"
echo "   3. Update your .env file (but don't commit it!)"
echo ""

echo "🔄 To set environment variable:"
echo "   export JWT_SECRET=\"[paste-your-key-here]\""