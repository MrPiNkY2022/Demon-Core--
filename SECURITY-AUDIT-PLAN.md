# Planned Security Audit Timeline – Demon Core 🔱

Phase                     | Date target     | Auditor / Firm               | Scope
-------------------------|-----------------|------------------------------|-----------------------------------------
Static code analysis     | Feb 2026        | Internal + Semgrep Pro       | All Kotlin, Rust, Solidity
JNI interface review     | Feb–Mar 2026    | Trail of Bits (Rust JNI)     | libcriticality.so + bindings
Smart contract audit     | Mar 2026        | OpenZeppelin Defender / Cantina | SoulContract, AuraContract, KarmaOracle
Mobile app pentest       | Apr 2026        | Cure53 or independent red team | Root detection bypass, Frida hooks, memory scraping
On-device ML model review| May 2026        | Private firm (TBD)           | MediaPipe aura model + TFLite weights
End-to-end simulation    | Jun 2026        | Internal war-game            | Adversary with physical device + chain analysis

Budget allocated: $180,000–240,000 USD (2026 est.)
