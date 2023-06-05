# Installation
## Windows

Run this script in Powershell
```powershell
$TBR_VERSION = 'build.5' # or latest version from releases tab

New-Item -ItemType Directory -Path "$env:APPDATA\tonbrett-cli"
Invoke-WebRequest https://github.com/DRSchlaubi/tonbrett-cli/releases/download/$TBR_VERSION/tonbrett-cli-windows-x64.exe -OutFile "$env:APPDATA\tonbrett-cli\tonbrett-cli.exe"
Add-Content -Path $profile.CurrentUserCurrentHost -Value '$env:Path+="C:\Program Files\tonbrett-cli\"'
$env:Path+="C:\Program Files\tonbrett-cli\"
```
