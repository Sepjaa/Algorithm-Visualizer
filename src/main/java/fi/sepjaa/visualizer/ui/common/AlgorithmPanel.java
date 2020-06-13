package fi.sepjaa.visualizer.ui.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jogamp.opengl.util.FPSAnimator;

import fi.sepjaa.visualizer.common.AlgorithmExecutor;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public abstract class AlgorithmPanel extends JPanel {

	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmPanel.class);

	private final FPSAnimator animator;
	private final Component visualizer;
	private final AlgorithmExecutor executor;

	private JPanel config;
	protected JButton startStop, randomize;

	public AlgorithmPanel(AlgorithmVisualizer visualizer, AlgorithmExecutor executor) {
		super(new MigLayout("insets 10", "[grow, fill][]", "[grow, fill]push[]"));
		setBackground(Color.GRAY.brighter());
		this.visualizer = visualizer.getComponent();
		this.executor = executor;
		this.animator = new FPSAnimator(visualizer, UiConstants.FPS, true);
	}

	@PostConstruct
	public void init() {
		setBackground(Color.GRAY.brighter());

		this.config = createConfig();
		add(config, "cell 1 0, wrap");

		this.startStop = new JButton(UiConstants.START_LBL);
		this.startStop.addActionListener(this::startStopAction);
		add(this.startStop, "cell 1 1, center, wrap");
		this.randomize = new JButton(UiConstants.RANDOMIZE_LBL);
		this.randomize.addActionListener(e -> createData());
		add(this.randomize, "cell 1 1, center");

		visualizer.setPreferredSize(
				new Dimension(UiConstants.PREFERRED_CANVAS_WIDTH, UiConstants.PREFERRED_CANVAS_HEIGHT));
		add(visualizer, "cell 0 0, span 1 2");

		validate();
		createData();

		animator.start();
	}

	@Override
	public void removeNotify() {
		destroy();
		super.removeNotify();
	}

	@PreDestroy
	public void destroy() {
		if (animator.isStarted()) {
			animator.stop();
		}
	}

	protected void startStopAction(ActionEvent e) {
		if (!executor.isExecuting()) {
			executor.start(run(), () -> {
				setConfigEnabled(true);
			});
			setConfigEnabled(false);
		} else {
			executor.stop();
		}
	}

	private void setConfigEnabled(boolean enabled) {
		SwingUtilities.invokeLater(() -> {
			startStop.setText(enabled ? UiConstants.START_LBL : UiConstants.STOP_LBL);
			randomize.setEnabled(enabled);
			for (Component c : this.config.getComponents()) {
				c.setEnabled(enabled);
			}
		});
	}

	public abstract String getTitle();

	protected abstract JPanel createConfig();

	protected abstract void createData();

	protected abstract Runnable run();
}
