/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.zest.dot.core.internal.antlr4;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.zest.dot.core.internal.DOTZestGraphEdgeModel;
import org.eclipse.zest.dot.core.internal.DOTZestGraphModel;
import org.eclipse.zest.dot.core.internal.DOTZestGraphNodeModel;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.A_listContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.Attr_listContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.Attr_stmtContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.EdgeRHSContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.Edge_stmtContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.GraphContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.Id_Context;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.Node_idContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.Node_stmtContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.SubgraphContext;

/**
 * Custom ANTLR visitor which transforms the AST of the DOT document into an
 * intermediate representation, which can then be used as input for a Zest
 * GraphViewer.
 */
public class DOTZestModelVisitor extends DOTBaseVisitor<Void> {
	private final Deque<Scope> scopes = new ArrayDeque<>();
	private final Map<String, DOTZestGraphNodeModel> nodes = new LinkedHashMap<>();
	private final List<DOTZestGraphEdgeModel> edges = new ArrayList<>();
	private boolean directed;

	@Override
	public Void visitGraph(GraphContext ctx) {
		directed = ctx.DIGRAPH() != null;

		try {
			scopes.push(new Scope());
			return super.visitGraph(ctx);
		} finally {
			scopes.pop();
		}
	}

	@Override
	public Void visitNode_stmt(Node_stmtContext ctx) {
		String id = getText(ctx.node_id().id_());

		DOTZestGraphNodeModel node = nodes.computeIfAbsent(id, this::createNode);

		if (ctx.attr_list() != null) {
			node.attributes().putAll(readAttributes(ctx.attr_list()));
		}

		return null;
	}

	@Override
	public Void visitEdge_stmt(Edge_stmtContext ctx) {
		List<String> endpoints = readEdges(ctx);

		if (endpoints.size() < 2) {
			return null;
		}

		Map<String, String> attributes = new LinkedHashMap<>();
		if (ctx.attr_list() != null) {
			attributes.putAll(readAttributes(ctx.attr_list()));
		}

		for (int i = 0; i < endpoints.size() - 1; ++i) {
			String sourceId = endpoints.get(i);
			String targetId = endpoints.get(i + 1);

			// Create implicit nodes
			nodes.computeIfAbsent(sourceId, this::createNode);
			nodes.computeIfAbsent(targetId, this::createNode);

			DOTZestGraphEdgeModel edge = createEdge(sourceId, targetId);
			edge.attributes().putAll(attributes);

			edges.add(edge);
		}

		return null;
	}

	@Override
	public Void visitAttr_stmt(Attr_stmtContext ctx) {
		Map<String, String> attributes = readAttributes(ctx.attr_list());

		if (ctx.NODE() != null) {
			getCurrentScope().nodeDefaults.putAll(attributes);
		}

		if (ctx.EDGE() != null) {
			getCurrentScope().edgeDefaults.putAll(attributes);
		}

		if (ctx.GRAPH() != null) {
			getCurrentScope().graphAttributes.putAll(attributes);
		}

		return null;
	}

	@Override
	public Void visitSubgraph(SubgraphContext ctx) {
		Scope parent = getCurrentScope();
		Scope child = new Scope();

		child.graphAttributes.putAll(parent.graphAttributes);
		child.nodeDefaults.putAll(parent.nodeDefaults);
		child.edgeDefaults.putAll(parent.edgeDefaults);

		try {
			scopes.push(child);
			return visitChildren(ctx);
		} finally {
			scopes.pop();
		}
	}

	private static Map<String, String> readAttributes(Attr_listContext ctx) {
		Map<String, String> result = new LinkedHashMap<>();

		for (A_listContext list : ctx.a_list()) {
			List<Id_Context> ids = list.id_();
			String key = getText(ids.get(0));
			String value = ""; //$NON-NLS-1$
			if (ids.size() > 1) {
				value = getText(ids.get(1));
			}
			result.put(key, value);
		}

		return result;
	}

	private static List<String> readEdges(Edge_stmtContext ctx) {
		List<String> edges = new ArrayList<>();

		// TODO Handle sub-graph
		edges.add(getText(ctx.node_id().id_()));

		EdgeRHSContext rhs = ctx.edgeRHS();
		for (Node_idContext node : rhs.node_id()) {
			// TODO Handle sub-graph
			edges.add(getText(node.id_()));
		}

		return edges;
	}

	private static String getText(Id_Context id) {
		String text = id.getText();
		if (text.startsWith("\"") && text.endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
			return text.substring(1, text.length() - 1);
		}
		return text;
	}

	private DOTZestGraphNodeModel createNode(String id) {
		DOTZestGraphNodeModel node = new DOTZestGraphNodeModel(id);
		node.attributes().putAll(getCurrentScope().nodeDefaults);
		return node;
	}

	private DOTZestGraphEdgeModel createEdge(String sourceId, String targetId) {
		DOTZestGraphEdgeModel edge = new DOTZestGraphEdgeModel(sourceId, targetId);
		edge.attributes().putAll(getCurrentScope().edgeDefaults);
		return edge;
	}

	private Scope getCurrentScope() {
		return scopes.peek();
	}

	public DOTZestGraphModel getGraphModel() {
		return new DOTZestGraphModel(directed, List.copyOf(nodes.values()), Collections.unmodifiableList(edges));
	}

	private static final class Scope {
		final Map<String, String> graphAttributes = new LinkedHashMap<>();
		final Map<String, String> nodeDefaults = new LinkedHashMap<>();
		final Map<String, String> edgeDefaults = new LinkedHashMap<>();
	}
}
