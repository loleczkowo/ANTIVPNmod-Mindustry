#!/usr/bin/env python3
import sys, subprocess, shlex, re

def is_openvpn(host: str, port: int = 443, host_timeout: int = 15) -> bool:
    cmd = (
        f'nmap -sV -Pn --version-intensity 5 --max-retries 1 '
        f'--host-timeout {host_timeout}s -p {port} {host}'
    )
    try:
        out = subprocess.check_output(
            shlex.split(cmd), stderr=subprocess.STDOUT, text=True
        )
    except subprocess.CalledProcessError as e:
        out = e.output
    pat = rf'^{port}/tcp\s+open\s+openvpn\b'
    return re.search(pat, out, re.IGNORECASE | re.MULTILINE) is not None

if __name__ == "__main__":
    host = sys.argv[1]
    if is_openvpn(host):
        print("VPN:TRUE")
    else:
        print("VPN:FALSE")
