package fi.sepjaa.visualizer.ui.common;

import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositiveIntegerVerifier extends InputVerifier {
	private static final Logger LOG = LoggerFactory.getLogger(PositiveIntegerVerifier.class);

	private final Consumer<Long> onVerified;

	public PositiveIntegerVerifier() {
		this.onVerified = (i) -> {
			return;
		};
	}

	public PositiveIntegerVerifier(Consumer<Long> onVerified) {
		this.onVerified = onVerified;
	}

	@Override
	public boolean verify(JComponent input) {
		if (input instanceof JFormattedTextField) {
			Object value = ((JFormattedTextField) input).getValue();
			long result = (long) value;
			accepted(input, result);
			return true;
		} else if (input instanceof JTextField) {
			String text = ((JTextComponent) input).getText();
			try {
				long result = Long.parseLong(text);
				accepted(input, result);
				return true;
			} catch (NumberFormatException e) {
				LOG.info("Error {}", e);
				declined(input);
				return false;
			}
		} else {
			LOG.warn("Invalid component {} for {}", input, this);
			return false;
		}
	}

	private void accepted(JComponent input, long value) {
		input.setBackground(Color.WHITE);
		onVerified.accept(value);
	}

	private void declined(JComponent input) {
		input.setBackground(new Color(255, 150, 150));
	}
}
