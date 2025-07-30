#!/usr/bin/env python3
import sys
import subprocess
import shlex
import re

def is_openvpn(host: str, port: int = 443, host_timeout: int = 15):
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

    # Match service line for the port
    pat = rf'^{port}/tcp\s+(open|closed|filtered)\s+(\S+)'
    m = re.search(pat, out, re.IGNORECASE | re.MULTILINE)

    if m:
        state, service = m.groups()
        if service.lower() == "openvpn":
            print(f"VPN:TRUE ({service})")
            return True
        else:
            print(f"VPN:FALSE ({service})")
            return False
    else:
        print("VPN:UNKNOWN (no service detected)")
        return False

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: antivpn.py <IP>")
        sys.exit(1)
    host = sys.argv[1]
    is_openvpn(host)
