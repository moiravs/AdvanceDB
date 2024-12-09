import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.ByteBuffer;

public class IntegerDeserializationScheme implements DeserializationSchema<Integer> {

    @Override
    public Integer deserialize(byte[] message) throws IOException {
        // Convert the 4-byte big-endian data into an Integer
        return ByteBuffer.wrap(message).getInt();
    }

    @Override
    public boolean isEndOfStream(Integer nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Integer> getProducedType() {
        return TypeInformation.of(Integer.class);
    }
}

