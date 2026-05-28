DELETE FROM public.batch_job_execution_params;
DELETE FROM public.batch_step_execution_context;
DELETE FROM public.batch_step_execution;
DELETE FROM public.batch_job_execution_context;
DELETE FROM public.batch_job_execution;
DELETE FROM public.batch_job_instance;

ALTER SEQUENCE BATCH_STEP_EXECUTION_SEQ RESTART WITH 1;
ALTER SEQUENCE BATCH_JOB_EXECUTION_SEQ RESTART WITH 1;
ALTER SEQUENCE BATCH_JOB_INSTANCE_SEQ RESTART WITH 1;