package es.eucm.gleaner.tracker.converter;

import es.eucm.gleaner.tracker.model.traces.InputTrace;
import es.eucm.gleaner.tracker.model.traces.LogicTrace;
import es.eucm.gleaner.tracker.model.traces.Trace;
import es.eucm.gleaner.tracker.model.traces.xapi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Transforms {@link Trace}s into {@link XAPIObject}s by adding its attributes
 * to the "extensions" map. "extensions" is a map inside the "definition"
 * attribute of the "object" in a statement. More info.
 * https://github.com/adlnet/xAPI-Spec/blob/master/xAPI.md#miscext.
 */
public class TracesConverter {

	private Map<Class<? extends Trace>, TraceConversor<? extends Trace>> conversors;
	private List<XAPIObject> statements;

	public TracesConverter() {
		conversors = new HashMap<Class<? extends Trace>, TraceConversor<? extends Trace>>();
		conversors.put(InputTrace.class, new InputTraceConverter());
		conversors.put(LogicTrace.class, new LogicTraceConverter());
		statements = new ArrayList<XAPIObject>();
	}

	public List<XAPIObject> convert(List<Trace> traces, String playerName) {
		statements.clear();
		for (Trace trace : traces) {
			TraceConversor conversor = conversors.get(trace.getClass());
			if (conversor == null) {
				continue;
			}

			XAPIObject xAPIObject = conversor.convert(trace, playerName);

			if (xAPIObject != null) {
				statements.add(xAPIObject);
			}
		}

		return statements;
	}

	public interface TraceConversor<T> {

		XAPIObject convert(T trace, String playerName);
	}
}