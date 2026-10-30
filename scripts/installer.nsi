; Per-user Windows installer. Build on Linux or Windows with makensis.
; APP_SOURCE must contain the verified portable app with its rebuilt core.
Unicode true
!include "MUI2.nsh"
!ifndef APP_SOURCE
 !error "Define APP_SOURCE"
!endif
!ifndef OUTPUT_FILE
 !define OUTPUT_FILE "OpenFlux-setup.exe"
!endif
Name "OpenFlux"
OutFile "${OUTPUT_FILE}"
InstallDir "$LOCALAPPDATA\Programs\OpenFlux"
RequestExecutionLevel user
SetCompressor /SOLID lzma
!define MUI_ABORTWARNING
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_LANGUAGE "Russian"
!insertmacro MUI_LANGUAGE "English"
Section "OpenFlux"
 SetOutPath "$INSTDIR"
 File /r "${APP_SOURCE}/*"
 WriteUninstaller "$INSTDIR\Uninstall.exe"
 CreateShortcut "$DESKTOP\OpenFlux.lnk" "$INSTDIR\OpenFlux.exe"
 CreateDirectory "$SMPROGRAMS\OpenFlux"
 CreateShortcut "$SMPROGRAMS\OpenFlux\OpenFlux.lnk" "$INSTDIR\OpenFlux.exe"
 CreateShortcut "$SMPROGRAMS\OpenFlux\Удалить.lnk" "$INSTDIR\Uninstall.exe"
 WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenFluxModern" "DisplayName" "OpenFlux 3.7.1"
 WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenFluxModern" "UninstallString" '"$INSTDIR\Uninstall.exe"'
SectionEnd
!include "${UNINSTALL_MANIFEST}"
Section "Uninstall"
 Delete "$DESKTOP\OpenFlux.lnk"
 RMDir /r "$SMPROGRAMS\OpenFlux"
 !insertmacro RemoveInstalledFiles
 Delete "$INSTDIR\Uninstall.exe"
 RMDir "$INSTDIR"
 DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenFluxModern"
 ; Profiles in APPDATA are intentionally preserved.
SectionEnd
