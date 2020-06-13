package fi.sepjaa.visualizer.ui.pathfinding;

import java.awt.Color;
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

	private final JTextField nodes, connections;
	private final JComboBox<Type> algorithmSelection;

	private final ImmutableMap<Type, PathfindingAlgorithm> algorithms;
	private final ImmutableList<PathfindingDataAware> dataListeners;
	private PathfindingData data;

	@Autowired
	public PathfindingPanel(PathfindingVisualizer visualizer, List<PathfindingAlgorithm> algorithms,
			AlgorithmExecutor executor, List<PathfindingDataAware> dataListeners,
			NodeSelectionUpdateDispatcher dispatcher) {
		super(visualizer, executor);
		this.dispatcher = dispatcher;
		this.dataListeners = ImmutableList.copyOf(dataListeners);
		algorithmSelection = new JComboBox<>();
		ImmutableMap.Builder<Type, PathfindingAlgorithm> builder = new ImmutableMap.Builder<>();
		for (PathfindingAlgorithm alg : algorithms) {
			builder.put(alg.getType(), alg);
			algorithmSelection.addItem(alg.getType());
		}
		this.algorithms = builder.build();

		nodes = new JTextField(String.valueOf(CommonConstants.DEFAULT_PATHFINDING_NODE_AMOUNT));
		nodes.setPreferredSize(new Dimension((int) 100, nodes.getHeight()));

		connections = new JTextField(String.valueOf(CommonConstants.DEFAULT_PATHFINDING_CONNECTIONS_AMOUNT));
		connections.setPreferredSize(new Dimension((int) 100, nodes.getHeight()));

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
		data = new PathfindingData(getNodesCount(), getConnectionsCount());
		dataListeners.forEach(listener -> listener.bind(data));
	}

	@Override
	public String getTitle() {
		return UiConstants.PATHFINDING_TAB_TITLE;
	}

	private PathfindingAlgorithm getAlgorithm() {
		Type type = (Type) algorithmSelection.getSelectedItem();
		return this.algorithms.get(type);
	}

	private int getNodesCount() {
		int amount = CommonConstants.DEFAULT_PATHFINDING_NODE_AMOUNT;
		try {
			amount = Integer.parseInt(nodes.getText());
		} catch (NumberFormatException e) {
			LOG.error("Invalid value in number of elements!");
		}
		return amount;
	}

	private int getConnectionsCount() {
		int amount = CommonConstants.DEFAULT_PATHFINDING_CONNECTIONS_AMOUNT;
		try {
			amount = Integer.parseInt(connections.getText());
		} catch (NumberFormatException e) {
			LOG.error("Invalid value in number of elements!");
		}
		return amount;
	}

	@Override
	protected JPanel createConfig() {
		JPanel config = new JPanel(new MigLayout("insets 0", "[][grow, fill]", "[][]15[]"));
		config.setBackground(Color.GRAY.brighter());

		config.add(new JLabel(UiConstants.NODES_LBL), "cell 0 0");
		config.add(nodes, "cell 1 0");

		config.add(new JLabel(UiConstants.CONNECTIONS_LBL), "cell 0 1");
		config.add(connections, "cell 1 1");

		config.add(new JLabel(UiConstants.ALGORITHM_LBL), "cell 0 2");
		config.add(algorithmSelection, "cell 1 2");
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
	public void selectionChanged(ImmutablePathfindingData newData) {
		if (newData.getStart() != CommonConstants.NO_STATEMENT && newData.getEnd() != CommonConstants.NO_STATEMENT) {
			SwingUtilities.invokeLater(() -> startStop.setEnabled(true));
		} else {
			SwingUtilities.invokeLater(() -> startStop.setEnabled(false));
		}
	}

}
