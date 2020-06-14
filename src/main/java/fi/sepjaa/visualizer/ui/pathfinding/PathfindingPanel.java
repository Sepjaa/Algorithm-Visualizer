package fi.sepjaa.visualizer.ui.pathfinding;

import java.awt.Dimension;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.common.AlgorithmExecutor;
import fi.sepjaa.visualizer.common.CommonConstants;
import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;
import fi.sepjaa.visualizer.pathfinding.algorithm.PathfindingAlgorithm;
import fi.sepjaa.visualizer.pathfinding.algorithm.PathfindingAlgorithm.Type;
import fi.sepjaa.visualizer.ui.common.AlgorithmPanel;
import fi.sepjaa.visualizer.ui.common.UiConstants;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
@Component
public class PathfindingPanel extends AlgorithmPanel implements PathfindingNodeSelectionListener {

	private static final Logger LOG = LoggerFactory.getLogger(PathfindingPanel.class);
	private final NodeSelectionUpdateDispatcher dispatcher;

	private final JTextField nodes, connections, measurementSleep, evaluationSleep;
	private final JComboBox<Type> algorithmSelection;

	private final ImmutableMap<Type, PathfindingAlgorithm> algorithms;
	private final ImmutableList<PathfindingDataListener> dataListeners;
	private PathfindingData data;

	private long nodeCount = CommonConstants.DEFAULT_PATHFINDING_NODE_AMOUNT;
	private long connectionCount = CommonConstants.DEFAULT_PATHFINDING_CONNECTIONS_AMOUNT;

	@Autowired
	public PathfindingPanel(PathfindingVisualizer visualizer, List<PathfindingAlgorithm> algorithms,
			AlgorithmExecutor executor, List<PathfindingDataListener> dataListeners,
			NodeSelectionUpdateDispatcher dispatcher) {
		super(visualizer, executor);
		this.dispatcher = dispatcher;
		this.dataListeners = ImmutableList.copyOf(dataListeners);
		this.algorithmSelection = new JComboBox<>();
		ImmutableMap.Builder<Type, PathfindingAlgorithm> builder = new ImmutableMap.Builder<>();
		for (PathfindingAlgorithm alg : algorithms) {
			builder.put(alg.getType(), alg);
			this.algorithmSelection.addItem(alg.getType());
		}
		this.algorithms = builder.build();

		this.nodes = UiConstants.getIntegerInstance(CommonConstants.DEFAULT_PATHFINDING_NODE_AMOUNT,
				i -> setNodesCount(i));
		this.nodes.setPreferredSize(new Dimension((int) 100, this.nodes.getHeight()));

		this.connections = UiConstants.getIntegerInstance(CommonConstants.DEFAULT_PATHFINDING_CONNECTIONS_AMOUNT,
				i -> setConnectionsCount(i));
		this.connections.setPreferredSize(new Dimension((int) 100, this.connections.getHeight()));

		this.measurementSleep = UiConstants.getIntegerInstance(CommonConstants.DEFAULT_MEASUREMENT_SLEEP,
				i -> setMeasurementSleep(i));
		this.measurementSleep.setPreferredSize(new Dimension((int) 100, this.measurementSleep.getHeight()));

		this.evaluationSleep = UiConstants.getIntegerInstance(CommonConstants.DEFAULT_EVALUATION_SLEEP,
				i -> setEvaluationSleep(i));
		this.evaluationSleep.setPreferredSize(new Dimension((int) 100, this.evaluationSleep.getHeight()));

		validate();
	}

	@Override
	@PostConstruct
	public void init() {
		super.init();
		startStop.setEnabled(false);
		dispatcher.addListener(this);
	}

	@Override
	@PreDestroy
	public void destroy() {
		super.destroy();
		dispatcher.removeListener(this);
	}

	@Override
	protected void createData() {
		if (data == null) {
			data = new PathfindingData(nodeCount, connectionCount);
		} else {
			ImmutablePathfindingData copy = data.getCopy();
			data = new PathfindingData(nodeCount, connectionCount, copy.getMeasurementSleep(),
					copy.getMeasurementSleep());
		}
		dataListeners.forEach(listener -> listener.bind(data));
	}

	private void setNodesCount(long count) {
		nodeCount = count;
	}

	private void setConnectionsCount(long count) {
		connectionCount = count;
	}

	private void setMeasurementSleep(long sleep) {
		data.setMeasurementSleep(sleep);
	}

	private void setEvaluationSleep(long sleep) {
		data.setEvaluationSleep(sleep);
	}

	@Override
	public String getTitle() {
		return UiConstants.PATHFINDING_TAB_TITLE;
	}

	private PathfindingAlgorithm getAlgorithm() {
		Type type = (Type) algorithmSelection.getSelectedItem();
		return this.algorithms.get(type);
	}

	@Override
	protected JPanel createConfig() {
		JPanel config = new JPanel(new MigLayout("insets 0", "[][grow, fill]", "[][]15[]15[][][]"));

		config.add(new JLabel(UiConstants.NODES_LBL), "cell 0 0");
		config.add(nodes, "cell 1 0");

		config.add(new JLabel(UiConstants.CONNECTIONS_LBL), "cell 0 1");
		config.add(connections, "cell 1 1");

		config.add(new JLabel(UiConstants.ALGORITHM_LBL), "cell 0 2");
		config.add(algorithmSelection, "cell 1 2");

		config.add(new JLabel(UiConstants.OPERATION_SLEEP_TIMES_LBL), "cell 0 3, span 2 1");

		config.add(new JLabel(UiConstants.MEASUREMENT_LBL), "cell 0 4");
		config.add(measurementSleep, "cell 1 4");

		config.add(new JLabel(UiConstants.EVALUATION_LBL), "cell 0 5");
		config.add(evaluationSleep, "cell 1 5");
		return config;
	}

	@Override
	protected Runnable run() {
		return () -> {
			LOG.info("Starting to run pathfind");
			getAlgorithm().find(data);
		};
	}

	@Override
	public void onEnd(boolean canceled) {
		data.clearMidFindInfo();
		super.onEnd(canceled);
	}

	@Override
	public void selectionChanged(ImmutablePathfindingData newData) {
		if (newData.getStart() != CommonConstants.NO_STATEMENT && newData.getEnd() != CommonConstants.NO_STATEMENT) {
			SwingUtilities.invokeLater(() -> startStop.setEnabled(true));
		} else {
			SwingUtilities.invokeLater(() -> startStop.setEnabled(false));
		}
	}

}
