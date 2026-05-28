# Code Standards

## General

- Keep modules small and single-purpose
- Fix root causes, do not layer workarounds
- Do not mix unrelated concerns in one component or route

## Java Conventions

- Each job configuration should import BatchInfrastructureConfig
- Configuration should be done using Spring @Bean Java methods rather than XML
