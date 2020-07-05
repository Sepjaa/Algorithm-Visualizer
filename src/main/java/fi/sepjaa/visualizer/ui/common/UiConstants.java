package fi.sepjaa.visualizer.ui.common;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.function.Consumer;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Various constants and UI vocabulary.
 *
 * @author Jaakko
 *
 */
public class UiConstants {

	public static final int PREFERRED_CANVAS_WIDTH = 1280;
	public static final int PREFERRED_CANVAS_HEIGHT = 720;
	public static final int FPS = 144;

	public static final int NODE_DRAW_SEGMENTS = 16;
	public static final float NODE_RADIUS = 0.01f;
	public static final float NODE_CONNECTION_WIDTH = 0.003f;
	public static final float BASE_NODE_SCALING = 40;

	public static final int AA_SAMPLES = 8;

	public static final String APPLICATION_TITLE = "Algorithm Visualizer";
	public static final String SORTING_TAB_TITLE = "Sorting";
	public static final String PATHFINDING_TAB_TITLE = "Pathfinding";
	public static final String START_LBL = "Start";
	public static final String STOP_LBL = "Stop";
	public static final String RANDOMIZE_LBL = "Randomize";

	public static final String ALGORITHM_LBL = "Algorithm";
	public static final String ELEMENTS_LBL = "Elements";
	public static final String NODES_LBL = "Nodes";
	public static final String CONNECTIONS_LBL = "Connections";
	public static final String OPERATION_SLEEP_TIMES_LBL = "Operation sleep times (μs)";
	public static final String COMPARISON_LBL = "Comparison";
	public static final String SWAP_LBL = "Swap";
	public static final String MEASUREMENT_LBL = "Measure";
	public static final String EVALUATION_LBL = "Evaluation";

	public static final Format INTEGER_FORMAT = new DecimalFormat("#,###");

	public static JTextField getIntegerInstance(long value) {
		return getIntegerInstance(value, (i) -> {
			return;
		});
	}

	public static JTextField getIntegerInstance(long value, Consumer<Long> onChange) {
		final JFormattedTextField result = new JFormattedTextField(INTEGER_FORMAT);
		result.setValue(value);
		result.addMouseListener(getJFTFCaretFixingMouseListener());
		result.getDocument().addDocumentListener(new PositiveIntegerValidator(result, INTEGER_FORMAT, onChange));
		return result;
	}

	private static MouseListener getJFTFCaretFixingMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				SwingUtilities.invokeLater(() -> {
					JFormattedTextField ftf = (JFormattedTextField) e.getSource();
					int offset = ftf.viewToModel2D(e.getPoint());
					ftf.setCaretPosition(offset);
				});
			}
		};
	}
}
