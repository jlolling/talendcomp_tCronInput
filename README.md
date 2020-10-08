# Talend Component tCronInput
Talend component to produce time series records based on a cron expression 
You can setup 6-part-cron expressions in the component and get records with following schema columns

| Column            | Content                         |
|-------------------|---------------------------------|
| PREV_TIMESTAMP    | The timestamp of the last event |
| NEXT_TIMESTAMP    | Timestamp of the next event     |
| CURRENT_INDEX     | Serial number of events         |
| NEXT_MINUTE       | Next event: minute              |
| NEXT_HOUR         | Next event: hour                |
| NEXT_DAY_OF_MONTH | Next event: day of month        |
| NEXT_MONTH        | Next event: month               |
| NEXT_DAY_OF_WEEK  | Next event: day of week         |
| NEXT_YEAR         | Next event: year                |

