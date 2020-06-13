package fi.sepjaa.visualizer.ui.common;

import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositiveIntegerVerifier extends InputVerifier {
	private static final Logger LOG = LoggerFactory.getLogger(PositiveIntegerVerifier.class);

	private final Consumer<Integer> onVerified;

	public PositiveIntegerVerifier(Consumer<Integer> onVerified) {
		this.onVerified = onVerified;
	}

	@Override
	public boolean verify(JComponent input) {
		if (input instanceof JTextComponent) {
			String text = ((JTextComponent) input).getText();
			try {
				int result = Integer.parseInt(text);
				input.setBackground(Color.WHITE);
				onVerified.accept(result);
				return true;
			} catch (NumberFormatException e) {
				input.setBackground(new Color(255, 150, 150));
				return false;
			}
		}
		LOG.warn("Invalid component {} for {}", input, this);
		return false;
	}
}
