# JWT Secret Key Generator for Windows PowerShell
# This script generates a secure random JWT secret key

Write-Host "🔐 JWT Secret Key Generator (Windows)" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host ""

# Method 1: Using .NET Crypto (Recommended for Windows)
Write-Host "Method 1: .NET RNGCryptoServiceProvider (Recommended)" -ForegroundColor Yellow
Add-Type -AssemblyName System.Security
$rng = New-Object System.Security.Cryptography.RNGCryptoServiceProvider
$bytes = New-Object byte[] 64
$rng.GetBytes($bytes)
$JWT_SECRET_DOTNET = [Convert]::ToBase64String($bytes)
Write-Host "JWT_SECRET=`"$JWT_SECRET_DOTNET`"" -ForegroundColor Cyan
Write-Host ""

# Method 2: Using System.Web.Security.Membership (Alternative)
Write-Host "Method 2: .NET Membership.GeneratePassword" -ForegroundColor Yellow
Add-Type -AssemblyName System.Web
$JWT_SECRET_MEMBERSHIP = [System.Web.Security.Membership]::GeneratePassword(86, 0)
Write-Host "JWT_SECRET=`"$JWT_SECRET_MEMBERSHIP`"" -ForegroundColor Cyan
Write-Host ""

# Method 3: Using GUID combination (Less secure but simple)
Write-Host "Method 3: Multiple GUIDs (Less secure)" -ForegroundColor Yellow
$guid1 = [System.Guid]::NewGuid().ToString("N")
$guid2 = [System.Guid]::NewGuid().ToString("N")
$JWT_SECRET_GUID = $guid1 + $guid2
Write-Host "JWT_SECRET=`"$JWT_SECRET_GUID`"" -ForegroundColor Cyan
Write-Host ""

Write-Host "⚠️  IMPORTANT SECURITY NOTES:" -ForegroundColor Red
Write-Host "   1. Never commit the generated key to version control" -ForegroundColor White
Write-Host "   2. Use different keys for development, staging, and production" -ForegroundColor White
Write-Host "   3. Store keys securely (Azure Key Vault, AWS Secrets Manager)" -ForegroundColor White
Write-Host "   4. Rotate keys regularly (every 6-12 months)" -ForegroundColor White
Write-Host "   5. Keep backup of the key in secure location" -ForegroundColor White
Write-Host ""

Write-Host "📋 To use the generated key:" -ForegroundColor Blue
Write-Host "   1. Copy one of the generated keys above" -ForegroundColor White
Write-Host "   2. Update your .env file with the new JWT_SECRET" -ForegroundColor White
Write-Host "   3. DO NOT commit .env file to git!" -ForegroundColor White
Write-Host ""

Write-Host "🔄 To set environment variable in PowerShell:" -ForegroundColor Blue
Write-Host "   `$env:JWT_SECRET = `"[paste-your-key-here]`"" -ForegroundColor White

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")