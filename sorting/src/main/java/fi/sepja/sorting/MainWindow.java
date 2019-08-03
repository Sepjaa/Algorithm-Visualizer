package fi.sepja.sorting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jogamp.opengl.util.FPSAnimator;

import fi.sepja.sorting.algorithms.InsertionSort;

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
	private static final int CANVAS_WIDTH = 1000;
	private static final int CANVAS_HEIGHT = 800;
	private static final int FPS = 144;

	private final Visualizer visualizer;
	private final JTextField elements;
	private final FPSAnimator animator;

	private Sorter sorter = null;

	public MainWindow() {
		visualizer = new Visualizer();
		visualizer.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		createSorter(String.valueOf(Sorter.DEFAULT_ELEMENT_AMOUNT));
		JPanel pane = new JPanel();
		pane.add(visualizer);
		animator = new FPSAnimator(visualizer, FPS, true);

		this.setLayout(new BorderLayout());
		this.getContentPane().add(pane, BorderLayout.CENTER);
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());

		JLabel desc = new JLabel("N:");
		buttons.add(desc);
		elements = new JTextField("100");
		elements.setPreferredSize(new Dimension(50, 25));
		elements.setText(String.valueOf(Sorter.DEFAULT_ELEMENT_AMOUNT));
		buttons.add(elements);

		JButton random = new JButton("Random");
		random.addActionListener((a) -> createSorter(elements.getText()));
		buttons.add(random);

		JButton sort = new JButton("Sort");
		sort.addActionListener((a) -> {
			if (sorter != null) {
				sorter.startSorting(new InsertionSort());
			} else {
				LOG.warn("Sorter should not be null");
				createSorter(elements.getText());
			}
		});
		buttons.add(sort);

		this.getContentPane().add(buttons, BorderLayout.NORTH);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.setTitle(TITLE);
		this.pack();
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

	private void createSorter(String string) {
		int amount = Sorter.DEFAULT_ELEMENT_AMOUNT;
		try {
			amount = Integer.parseInt(string);
		} catch (NumberFormatException e) {
			LOG.error("Invalid value in number of elements!");
		}
		if (sorter != null) {
			sorter.destroy();
		}
		sorter = new Sorter(visualizer, amount);
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
