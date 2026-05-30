# Progress Tracker

Update this file after every meaningful implementation change.

## Current Phase

- In progress

## Current Goal

- Add instructional Spring Batch jobs and tests that demonstrate core batch concepts.

## Completed

- Renamed the instructional jobs to semantic names: `HelloWorldJob`, `ProviderLoadJob`, and `ProviderRestartDemoJob`.
- Added `ProviderRestartDemoJob` as a restartability demo job.
- Added a focused H2-backed restart test for `ProviderRestartDemoJob`.
- Added `ProviderFilterJob` as a filtering demo job with invalid-but-readable input records.
- Added a focused H2-backed filter test that asserts read, filter, write, and database counts.
- Renamed provider input data folders from numbered job names to semantic package-aligned names.
- Added `ProviderSkipDemoJob` as a fault-tolerant process-skip demo job.
- Added a focused H2-backed skip test that asserts read, filter, process-skip, write, and database counts.
- Renamed the filtering demo classes, package, beans, and input folder to use `filter` terminology.
- Removed generic step and chunk listeners from the filter and skip demos to keep those lessons focused.
- Added `ProviderRetryDemoJob` as a fault-tolerant retry demo job for transient process failures.
- Added a focused H2-backed retry test that asserts successful retry, no skips, and full database writes.

## In Progress

- Validate `ProviderRestartDemoJob` from Eclipse or Maven once the local Maven wrapper issue is resolved.

## Next Up

- Consider adding a short README section for each available job.
- Consider adding a conditional-flow demo job.

## Open Questions

- Decide whether restart demos should continue reusing `ncmmis_provider` or eventually move to dedicated instructional tables.

## Architecture Decisions

- `ProviderRestartDemoJob` intentionally reuses `ncmmis_provider` so restart behavior is visible without adding another business table.
- `ProviderRestartDemoJob` omits `RunIdIncrementer` so a failed job instance can be restarted with the same identifying parameters.

## Session Notes

- The first `ProviderRestartDemoJob` execution fails at provider id `350`, after three chunks have committed. A restart should resume from the last committed chunk and finish the load.
