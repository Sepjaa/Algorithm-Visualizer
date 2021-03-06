package fi.sepjaa.visualizer.ui.common;

import java.awt.Color;
import java.text.Format;
import java.text.ParseException;
import java.util.function.Consumer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates the input value on every change and fires callbacks from EDT with
 * validated values. Pretty hackish but default formatter/input verifier works
 * badly.
 *
 * @author Jaakko
 *
 */
public class PositiveIntegerValidator implements DocumentListener {
	private static final Logger LOG = LoggerFactory.getLogger(PositiveIntegerValidator.class);
	private final JTextComponent field;
	private final Format format;
	private final Consumer<Long> onChange;

	public PositiveIntegerValidator(JTextComponent field, Format format, Consumer<Long> onChange) {
		super();
		this.field = field;
		this.format = format;
		this.onChange = onChange;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		onChange(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		onChange(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		onChange(e);
	}

	private void onChange(DocumentEvent event) {
		String text = null;
		try {
			text = event.getDocument().getText(0, event.getDocument().getLength());
		} catch (BadLocationException e) {
			validationError();
		}
		LOG.debug("Text value:{}", text);
		if (text.isEmpty() || containsIllegalCharacters(text)) {
			validationError();
			return;
		}
		try {
			long value = (long) format.parseObject(text);
			LOG.debug("Value:{}", value);
			if (value < 0) {
				validationError();
			} else {
				validated(value);
			}
		} catch (ParseException | ClassCastException e) {
			validationError();
		}
	}

	private boolean containsIllegalCharacters(String text) {
		try {
			this.format.parseObject(text);
			return false;
		} catch (ParseException e) {
			return true;
		}
	}

	private void validationError() {
		this.field.setBackground(Color.RED.brighter());
	}

	private void validated(long value) {
		this.field.setBackground(Color.WHITE);
		this.onChange.accept(value);
	}

}
