# Installation
## Windows

**Note:** Since conhost is a piece of junk you need the new [Windows Terminal App](https://apps.microsoft.com/store/detail/windows-terminal/9N0DX20HK701)

Run this script in Powershell
```powershell
$TBR_VERSION = 'build.5' # or latest version from releases tab

New-Item -ItemType File -Path $profile.CurrentUserCurrentHost -Force
Invoke-WebRequest https://github.com/DRSchlaubi/tonbrett-cli/releases/download/$TBR_VERSION/tonbrett-cli-windows-x64.exe -OutFile "$env:APPDATA\tonbrett-cli\tonbrett-cli.exe"
$PathVariable = ';' + $env:APPDATA + '\tonbrett-cli\tonbrett-cli.exe"'
Add-Content -Path $profile.CurrentUserCurrentHost -Value ('$env:Path+=' + $PathVariable)
$env:Path+=$PathVariable
```
