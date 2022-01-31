package com.rbkmoney.fistful.reporter.kafka.serdes;

import dev.vality.fistful.destination.TimestampedChange;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import org.springframework.stereotype.Service;

@Service
public class DestinationChangeMachineEventParser extends MachineEventParser<TimestampedChange> {

    public DestinationChangeMachineEventParser(BinaryDeserializer<TimestampedChange> deserializer) {
        super(deserializer);
    }
}