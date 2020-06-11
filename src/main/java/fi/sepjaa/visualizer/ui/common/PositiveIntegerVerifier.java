package fi.sepjaa.visualizer.ui.common;

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositiveIntegerVerifier extends InputVerifier {
	private static final Logger LOG = LoggerFactory.getLogger(PositiveIntegerVerifier.class);

	private final Runnable onVerified;

	public PositiveIntegerVerifier(Runnable onVerified) {
		this.onVerified = onVerified;
	}

	@Override
	public boolean verify(JComponent input) {
		if (input instanceof JTextComponent) {
			String text = ((JTextComponent) input).getText();
			try {
				Integer.parseInt(text);
				input.setBackground(Color.WHITE);
				onVerified.run();
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
