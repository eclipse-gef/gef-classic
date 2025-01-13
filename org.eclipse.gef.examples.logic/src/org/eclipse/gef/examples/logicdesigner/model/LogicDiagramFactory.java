/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.examples.logicdesigner.model;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.gef.requests.CreationFactory;

public class LogicDiagramFactory {

	LogicDiagram root;

	protected static void connect(LogicSubpart e1, String t1, LogicSubpart e2, String t2) {
		Wire wire = new Wire();
		wire.setSource(e1);
		wire.setSourceTerminal(t1);
		wire.setTarget(e2);
		wire.setTargetTerminal(t2);
		wire.attachSource();
		wire.attachTarget();
	}

	public static Circuit createFullAdder() {
		final Gate or;
		final Circuit circuit, circuit1, circuit2;

		circuit1 = createHalfAdder();
		circuit2 = createHalfAdder();
		circuit1.setLocation(new Point(4, 20));
		circuit2.setLocation(new Point(76, 180));

		circuit = new Circuit();
		circuit.setSize(new Dimension(240, 432));
		or = new OrGate();
		or.setLocation(new Point(44, 324));

		circuit.addChild(circuit1);
		circuit.addChild(circuit2);

		connect(circuit, Circuit.TERMINALS_OUT[0], circuit1, Circuit.TERMINALS_IN[0]);
		connect(circuit, Circuit.TERMINALS_OUT[2], circuit1, Circuit.TERMINALS_IN[3]);
		connect(circuit, Circuit.TERMINALS_OUT[3], circuit2, Circuit.TERMINALS_IN[3]);
		connect(circuit1, Circuit.TERMINALS_OUT[7], circuit2, Circuit.TERMINALS_IN[0]);

		circuit.addChild(or);
		connect(or, SimpleOutput.TERMINAL_OUT, circuit, Circuit.TERMINALS_IN[4]);
		connect(circuit1, Circuit.TERMINALS_OUT[4], or, Gate.TERMINAL_A);
		connect(circuit2, Circuit.TERMINALS_OUT[4], or, Gate.TERMINAL_B);
		connect(circuit2, Circuit.TERMINALS_OUT[7], circuit, Circuit.TERMINALS_IN[7]);

		return circuit;
	}

	public static Circuit createHalfAdder() {
		Gate and, xor;
		Circuit circuit;

		circuit = new Circuit();
		circuit.setSize(new Dimension(120, 140));
		and = new AndGate();
		and.setLocation(new Point(4, 24));
		xor = new XORGate();
		xor.setLocation(new Point(44, 24));

		circuit.addChild(xor);
		circuit.addChild(and);

		connect(circuit, Circuit.TERMINALS_OUT[0], and, Gate.TERMINAL_A);
		connect(circuit, Circuit.TERMINALS_OUT[3], and, Gate.TERMINAL_B);
		connect(circuit, Circuit.TERMINALS_OUT[0], xor, Gate.TERMINAL_A);
		connect(circuit, Circuit.TERMINALS_OUT[3], xor, Gate.TERMINAL_B);

		connect(and, SimpleOutput.TERMINAL_OUT, circuit, Circuit.TERMINALS_IN[4]);
		connect(xor, SimpleOutput.TERMINAL_OUT, circuit, Circuit.TERMINALS_IN[7]);
		return circuit;
	}

	public static Object createLargeModel() {
		LogicDiagram root = new LogicDiagram();

		final Circuit circuit1, circuit2, circuit3, circuit4;
		final LED led1, led2, led3;

		led1 = new LED();
		led1.setValue(3);
		led2 = new LED();
		led2.setValue(7);
		led3 = new LED();
		led1.setLocation(new Point(340, 32));
		led2.setLocation(new Point(640, 32));
		led3.setLocation(new Point(490, 720));
		root.addChild(led1);
		root.addChild(led2);
		root.addChild(led3);
		//
		circuit1 = createHalfAdder();
		circuit1.setSize(new Dimension(128, 432));
		circuit1.setLocation(new Point(910, 208));
		root.addChild(circuit1);
		//
		circuit2 = createFullAdder();
		circuit2.setLocation(new Point(610, 208));
		root.addChild(circuit2);
		connect(circuit1, Circuit.TERMINALS_OUT[4], circuit2, Circuit.TERMINALS_IN[3]);
		//
		circuit3 = createFullAdder();
		circuit3.setLocation(new Point(310, 208));
		root.addChild(circuit3);
		connect(circuit2, Circuit.TERMINALS_OUT[4], circuit3, Circuit.TERMINALS_IN[3]);
		//
		circuit4 = createFullAdder();
		circuit4.setLocation(new Point(10, 208));
		//
		connect(led1, LED.TERMINAL_1_OUT, circuit1, Circuit.TERMINALS_IN[0]);
		connect(led1, LED.TERMINAL_2_OUT, circuit2, Circuit.TERMINALS_IN[0]);
		connect(led1, LED.TERMINAL_3_OUT, circuit3, Circuit.TERMINALS_IN[0]);
		connect(led2, LED.TERMINAL_1_OUT, circuit1, Circuit.TERMINALS_IN[3]);
		connect(led2, LED.TERMINAL_2_OUT, circuit2, Circuit.TERMINALS_IN[2]);
		connect(circuit1, Circuit.TERMINALS_OUT[7], led3, LED.TERMINAL_1_IN);
		connect(circuit2, Circuit.TERMINALS_OUT[7], led3, LED.TERMINAL_2_IN);
		connect(circuit3, Circuit.TERMINALS_OUT[7], led3, LED.TERMINAL_3_IN);
		//
		connect(led2, LED.TERMINAL_3_OUT, circuit3, Circuit.TERMINALS_IN[2]);
		root.addChild(circuit4);
		connect(led2, LED.TERMINAL_4_OUT, circuit4, Circuit.TERMINALS_IN[2]);
		connect(circuit3, Circuit.TERMINALS_OUT[4], circuit4, Circuit.TERMINALS_IN[3]);
		connect(led1, LED.TERMINAL_4_OUT, circuit4, Circuit.TERMINALS_IN[0]);
		connect(circuit4, Circuit.TERMINALS_OUT[7], led3, LED.TERMINAL_4_IN);

		return root;
	}

	public static CreationFactory getFullAdderFactory() {
		return new CreationFactory() {
			@Override
			public Object getNewObject() {
				return createFullAdder();
			}

			@Override
			public Object getObjectType() {
				return "Full Adder"; //$NON-NLS-1$
			}
		};
	}

	public static CreationFactory getHalfAdderFactory() {
		return new CreationFactory() {
			@Override
			public Object getNewObject() {
				return createHalfAdder();
			}

			@Override
			public Object getObjectType() {
				return "Half Adder"; //$NON-NLS-1$
			}
		};
	}

	public Object createEmptyModel() {
		root = new LogicDiagram();
		return root;
	}

	static public Object createModel() {

		LogicDiagram root = new LogicDiagram();

		Circuit circuit1;

		circuit1 = createHalfAdder();
		circuit1.setLocation(new Point(50, 50));
		root.addChild(circuit1);
		return root;
	}

	public Object getRootElement() {
		if (root == null) {
			createLargeModel();
		}
		return root;
	}

}
