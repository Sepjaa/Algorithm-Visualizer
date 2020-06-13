package fi.sepjaa.visualizer.ui.sorting;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.common.AlgorithmExecutor;
import fi.sepjaa.visualizer.common.CommonConstants;
import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.SortingAlgorithm;
import fi.sepjaa.visualizer.sorting.algorithm.SortingAlgorithm.Type;
import fi.sepjaa.visualizer.ui.common.AlgorithmPanel;
import fi.sepjaa.visualizer.ui.common.PositiveIntegerVerifier;
import fi.sepjaa.visualizer.ui.common.UiConstants;
import net.miginfocom.swing.MigLayout;

/**
 * Panel containing a {@link SortingVisualizer}, the sort configuration
 * components and a start/stop button.
 *
 * @author Jaakko
 *
 */
@SuppressWarnings("serial")
@Component
public class SortingPanel extends AlgorithmPanel {
	private static final Logger LOG = LoggerFactory.getLogger(SortingPanel.class);

	private final JTextField elements, comparisonSleep, swapSleep;
	private final JComboBox<Type> algorithmSelection;
	private final ImmutableMap<Type, SortingAlgorithm> algorithms;
	private final SortingVisualizer visualizer;
	private SortingData data;

	@Autowired
	public SortingPanel(SortingVisualizer visualizer, List<SortingAlgorithm> algorithms, AlgorithmExecutor executor) {
		super(visualizer, executor);
		this.visualizer = visualizer;
		algorithmSelection = new JComboBox<>();
		ImmutableMap.Builder<Type, SortingAlgorithm> builder = new ImmutableMap.Builder<>();
		for (SortingAlgorithm alg : algorithms) {
			builder.put(alg.getType(), alg);
			algorithmSelection.addItem(alg.getType());
		}
		this.algorithms = builder.build();

		elements = new JTextField(String.valueOf(CommonConstants.DEFAULT_SORTING_ELEMENT_AMOUNT));
		elements.setPreferredSize(new Dimension((int) 100, elements.getHeight()));

		comparisonSleep = new JTextField(String.valueOf(CommonConstants.DEFAULT_COMPARISON_SLEEP));
		comparisonSleep.setPreferredSize(new Dimension((int) 100, comparisonSleep.getHeight()));
		comparisonSleep.setInputVerifier(new PositiveIntegerVerifier(i -> setCompareSleep()));

		swapSleep = new JTextField(String.valueOf(CommonConstants.DEFAULT_SWAP_SLEEP));
		swapSleep.setPreferredSize(new Dimension((int) 100, swapSleep.getHeight()));
		swapSleep.setInputVerifier(new PositiveIntegerVerifier(i -> setSwapSleep()));

		algorithmSelection.addActionListener(a -> {
			createData();
			visualizer.setDrawMemory(getAlgorithm().isFullMemoryBufferAlgorithm());
		});

		validate();
	}

	@Override
	protected void createData() {
		data = new SortingData(getElementsCount(), getAlgorithm());
		setCompareSleep();
		setSwapSleep();
		visualizer.bind(data);
	}

	@Override
	public String getTitle() {
		return UiConstants.SORTING_TAB_TITLE;
	}

	private SortingAlgorithm getAlgorithm() {
		Type type = (Type) algorithmSelection.getSelectedItem();
		return this.algorithms.get(type);
	}

	private int getElementsCount() {
		int amount = CommonConstants.DEFAULT_SORTING_ELEMENT_AMOUNT;
		try {
			amount = Integer.parseInt(elements.getText());
		} catch (NumberFormatException e) {
			LOG.error("Invalid value in number of elements!");
		}
		return amount;
	}

	private void setCompareSleep() {
		try {
			int amount = Integer.parseInt(comparisonSleep.getText());
			data.setCompareSleep(amount);
		} catch (NumberFormatException e) {
			LOG.warn("Invalid value in comparison sleep!");
		}
	}

	private void setSwapSleep() {
		try {
			int amount = Integer.parseInt(swapSleep.getText());
			data.setSwapSleep(amount);
		} catch (NumberFormatException e) {
			LOG.warn("Invalid value in swap sleep!");
		}
	}

	@Override
	protected JPanel createConfig() {
		JPanel config = new JPanel(new MigLayout("insets 0", "[][grow, fill]", "[]15[]15[][][]"));

		config.add(new JLabel(UiConstants.ELEMENTS_LBL), "cell 0 0");
		config.add(elements, "cell 1 0");

		config.add(new JLabel(UiConstants.ALGORITHM_LBL), "cell 0 1");
		config.add(algorithmSelection, "cell 1 1");

		config.add(new JLabel(UiConstants.OPERATION_SLEEP_TIMES_LBL), "cell 0 2, span 2 1");

		config.add(new JLabel(UiConstants.COMPARISON_LBL), "cell 0 3");
		config.add(comparisonSleep, "cell 1 3");

		config.add(new JLabel(UiConstants.SWAP_LBL), "cell 0 4");
		config.add(swapSleep, "cell 1 4");
		return config;
	}

	@Override
	protected Runnable run() {
		return () -> {
			LOG.info("Starting to run sort");
			getAlgorithm().sort(data);
			data.clearNexts();
		};
	}
}
