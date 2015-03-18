#!/bin/bash
set -ex

exec {{ df_platform_home }}/bin/dfp ingest-hl7-server \
    --port 9520 \
    -i "urn:df-institution:UCSF" \
    -f "urn:df-facility:test" \
    -s "urn:df-ingestSource:HL7v24" \
    -p "HL7v24" \
    -k "$KAFKA_BROKERS" \
    -q "hl7Message"
