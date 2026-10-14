#!/usr/bin/env python3
"""Упаковать проверенный portable GUI в per-user NSIS installer."""
import argparse
from pathlib import Path
import subprocess
import tempfile

parser = argparse.ArgumentParser()
parser.add_argument('source', type=Path)
parser.add_argument('output', type=Path)
args = parser.parse_args()
source, output = args.source.resolve(), args.output.resolve()
if not (source / 'OpenFlux.exe').is_file():
    raise SystemExit('Нет OpenFlux.exe в исходном каталоге')
output.parent.mkdir(parents=True, exist_ok=True)
with tempfile.TemporaryDirectory(prefix='openflux-installer-', dir=output.parent) as tmp:
    lines = ['!macro RemoveInstalledFiles']
    paths = sorted(source.rglob('*'))
    for path in paths:
        relative = str(path.relative_to(source)).replace('/', '\\')
        if any(c in relative for c in ['"', '$', '\n', '\r']):
            raise SystemExit('Небезопасное имя файла в portable')
        if path.is_file():
            lines.append(f' Delete "$INSTDIR\\{relative}"')
    for path in sorted((p for p in paths if p.is_dir()), key=lambda p: len(p.parts), reverse=True):
        relative = str(path.relative_to(source)).replace('/', '\\')
        lines.append(f' RMDir "$INSTDIR\\{relative}"')
    lines.append('!macroend')
    manifest = Path(tmp) / 'uninstall.nsh'
    manifest.write_text('\n'.join(lines) + '\n')
    subprocess.run(['makensis', f'-DAPP_SOURCE={source}', f'-DOUTPUT_FILE={output}',
                    f'-DUNINSTALL_MANIFEST={manifest}', str(Path(__file__).with_name('installer.nsi'))], check=True)
