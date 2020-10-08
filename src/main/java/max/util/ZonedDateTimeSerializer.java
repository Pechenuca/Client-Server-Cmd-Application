package max.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer extends XmlAdapter<String, ZonedDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    @Override
    public ZonedDateTime unmarshal(String v) throws Exception {
        return formatter.parse(v, ZonedDateTime::from);
    }

    @Override
    public String marshal(ZonedDateTime v) throws Exception {
        return formatter.format(v);
    }
}
