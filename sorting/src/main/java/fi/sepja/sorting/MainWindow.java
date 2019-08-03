package fi.sepja.sorting;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jogamp.opengl.util.FPSAnimator;

import fi.sepja.sorting.algorithms.Algorithm;
import fi.sepja.sorting.algorithms.Algorithm.AlgorithmType;
import fi.sepja.sorting.algorithms.impl.AlgorithmDeployment;
import net.miginfocom.swing.MigLayout;

/**
 * Mainwindow and controller for the application.
 *
 * @author Jaakko
 *
 */
public class MainWindow extends JFrame {
	private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);
	/**
	 *
	 */
	private static final long serialVersionUID = 5118488474777349693L;
	private static String TITLE = "Sorting Visualizer";
	private static final int CANVAS_WIDTH = 600;
	private static final int CANVAS_HEIGHT = 400;
	private static final int FPS = 144;

	private final Visualizer visualizer;
	private final JTextField elements, comparisonSleep, swapSleep;
	private final FPSAnimator animator;
	private final JComboBox<AlgorithmType> algorithm;

	private Sorter sorter = null;

	public MainWindow() {
		super();

		this.getContentPane().setLayout(new MigLayout("insets 10", "[grow, fill][]", "[grow, fill]"));
		this.getContentPane().setBackground(Color.GRAY.brighter());

		visualizer = new Visualizer();
		visualizer.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		this.getContentPane().add(visualizer, "cell 0 0");

		animator = new FPSAnimator(visualizer, FPS, true);

		JPanel hudPanel = new JPanel(new MigLayout("insets 0", "[grow, fill]", "push[]40[]20[]push"));
		hudPanel.setBackground(Color.GRAY.brighter());

		JPanel config = new JPanel(new MigLayout("insets 0", "[][grow, fill]", "[]15[][][]"));
		config.setBackground(Color.GRAY.brighter());

		config.add(new JLabel("Elements"), "cell 0 0");
		elements = new JTextField(String.valueOf(Sorter.DEFAULT_ELEMENT_AMOUNT));
		elements.setPreferredSize(new Dimension((int) 100, elements.getHeight()));
		config.add(elements, "cell 1 0");

		config.add(new JLabel("Algorithm"), "cell 0 1");
		algorithm = new JComboBox<>(AlgorithmDeployment.getAlgorithms());
		config.add(algorithm, "cell 1 1");

		config.add(new JLabel("Operation sleep times (μs)"), "cell 0 2, span 2 1");

		config.add(new JLabel("Comparison"), "cell 0 3");
		comparisonSleep = new JTextField(String.valueOf(Sorter.DEFAULT_COMPARISON_SLEEP));
		comparisonSleep.setPreferredSize(new Dimension((int) 100, elements.getHeight()));
		config.add(comparisonSleep, "cell 1 3");

		config.add(new JLabel("Swap"), "cell 0 4");
		swapSleep = new JTextField(String.valueOf(Sorter.DEFAULT_SWAP_SLEEP));
		swapSleep.setPreferredSize(new Dimension((int) 100, elements.getHeight()));
		config.add(swapSleep, "cell 1 4");

		hudPanel.add(config, "cell 0 0");

		JButton random = new JButton("Randomize");
		random.addActionListener((a) -> createSorter());
		hudPanel.add(random, "cell 0 1, grow");

		JButton sort = new JButton("Sort");
		sort.addActionListener((a) -> start());
		hudPanel.add(sort, "cell 0 2, grow");

		add(hudPanel, "cell 1 0");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.setTitle(TITLE);
		this.pack();
		createSorter();
		this.setVisible(true);
		animator.start();
	}

	@Override
	public void dispose() {
		destroy();
		super.dispose();
	}

	public void destroy() {
		LOG.info("Destroying {}", this);
		if (animator.isStarted()) {
			animator.stop();
		}
		visualizer.destroy();
		if (sorter != null) {
			sorter.destroy();
		}
	}

	private void start() {
		if (sorter == null) {
			LOG.error("Sorter null on start!");
			createSorter();
		}
		sorter.startSorting(getAlgorithm());
	}

	private Algorithm getAlgorithm() {
		AlgorithmType type = (AlgorithmType) algorithm.getSelectedItem();
		return AlgorithmDeployment.getImplementationFor(type).setDelays(getCompareSleep(), getSwapSleep());
	}

	private void createSorter() {
		if (sorter != null) {
			sorter.destroy();
		}
		sorter = new Sorter(visualizer, getElementsCount());
	}

	private int getElementsCount() {
		int amount = Sorter.DEFAULT_ELEMENT_AMOUNT;
		try {
			amount = Integer.parseInt(elements.getText());
		} catch (NumberFormatException e) {
			LOG.error("Invalid value in number of elements!");
		}
		return amount;
	}

	private int getCompareSleep() {
		int amount = Sorter.DEFAULT_COMPARISON_SLEEP;
		try {
			amount = Integer.parseInt(comparisonSleep.getText());
		} catch (NumberFormatException e) {
			LOG.error("Invalid value in comparison sleep!");
		}
		return amount;
	}

	private int getSwapSleep() {
		int amount = Sorter.DEFAULT_SWAP_SLEEP;
		try {
			amount = Integer.parseInt(swapSleep.getText());
		} catch (NumberFormatException e) {
			LOG.error("Invalid value in swap sleep!");
		}
		return amount;
	}

	/**
	 * The entry main() method
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainWindow();
			}
		});
	}
}
