package fi.sepjaa.visualizer.ui.pathfinding;

import java.awt.Color;
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
import fi.sepjaa.visualizer.pathfinding.PathfindingData;
import fi.sepjaa.visualizer.pathfinding.algorithm.PathfindingAlgorithm;
import fi.sepjaa.visualizer.pathfinding.algorithm.PathfindingAlgorithm.Type;
import fi.sepjaa.visualizer.ui.common.AlgorithmPanel;
import fi.sepjaa.visualizer.ui.common.UiConstants;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
@Component
public class PathfindingPanel extends AlgorithmPanel {

	private static final Logger LOG = LoggerFactory.getLogger(PathfindingPanel.class);

	private final JTextField nodes, connections;
	private final JComboBox<Type> algorithmSelection;

	private final PathfindingVisualizer visualizer;

	private final ImmutableMap<Type, PathfindingAlgorithm> algorithms;

	private PathfindingData data;

	@Autowired
	public PathfindingPanel(PathfindingVisualizer visualizer, List<PathfindingAlgorithm> algorithms,
			AlgorithmExecutor executor) {
		super(visualizer, executor);
		this.visualizer = visualizer;
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
	protected void createData() {
		data = new PathfindingData(getNodesCount(), getConnectionsCount());
		visualizer.bind(data);
	}

	@Override
	public String getTitle() {
		return UiConstants.PATHFINDING_TITLE;
	}

	@Override
	public void removeNotify() {
		destroy();
		super.removeNotify();
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
		JPanel config = new JPanel(new MigLayout("insets 0", "[][grow, fill]", "[][]"));
		config.setBackground(Color.GRAY.brighter());

		config.add(new JLabel("Nodes "), "cell 0 0");
		config.add(nodes, "cell 1 0");

		config.add(new JLabel("Connections"), "cell 0 1");
		config.add(connections, "cell 1 1");
		return config;
	}

	@Override
	protected Runnable run() {
		throw new UnsupportedOperationException("No implementation for pathfind.");
	}

}
