# Progress Tracker

Update this file after every meaningful implementation change.

## Current Phase

- In progress

## Current Goal

- Add instructional Spring Batch jobs and tests that demonstrate core batch concepts.

## Completed

- Added `ProviderJob3` as a restartability demo job.
- Added a focused H2-backed restart test for `ProviderJob3`.

## In Progress

- Validate `ProviderJob3` from Eclipse or Maven once the local Maven wrapper issue is resolved.

## Next Up

- Consider adding a short README section for each available job.

## Open Questions

- Decide whether restart demos should continue reusing `ncmmis_provider` or eventually move to dedicated instructional tables.

## Architecture Decisions

- `ProviderJob3` intentionally reuses `ncmmis_provider` so restart behavior is visible without adding another business table.
- `ProviderJob3` omits `RunIdIncrementer` so a failed job instance can be restarted with the same identifying parameters.

## Session Notes

- The first `ProviderJob3` execution fails at provider id `350`, after three chunks have committed. A restart should resume from the last committed chunk and finish the load.
