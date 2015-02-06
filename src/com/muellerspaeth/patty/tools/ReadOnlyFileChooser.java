package com.muellerspaeth.patty.tools;

import darrylbu.util.SwingUtils;

import java.awt.Container;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.*;

public class ReadOnlyFileChooser extends JFileChooser {

	private static final long serialVersionUID = 4524470426754801046L;

	@SuppressWarnings({ "rawtypes", "restriction" })
	public ReadOnlyFileChooser(File home) {
		final JFileChooser chooser = new JFileChooser(home);
		InputMap inputMap = chooser
				.getInputMap(JFileChooser.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke("F2"), "none");

		final JList list = SwingUtils.getDescendantOfType(JList.class, chooser,
				"Enabled", true);
		final MouseListener mouseListener = list.getMouseListeners()[2];
		list.removeMouseListener(mouseListener);
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					mouseListener.mouseClicked(e);
				}
			}
		});

		final Container filePane = SwingUtilities.getAncestorOfClass(
				sun.swing.FilePane.class, list);
		filePane.addContainerListener(new ContainerAdapter() {

			@Override
			public void componentAdded(ContainerEvent e) {
				final JTable table = SwingUtils.getDescendantOfType(
						JTable.class, chooser, "Enabled", true);
				if (table != null
						&& table.getPropertyChangeListeners("tableCellEditor").length == 0) {
					table.addPropertyChangeListener("tableCellEditor",
							new PropertyChangeListener() {

								@Override
								public void propertyChange(
										PropertyChangeEvent evt) {
									SwingUtilities.invokeLater(new Runnable() {

										@Override
										public void run() {
											if (table.isEditing()) {
												table.getCellEditor()
														.stopCellEditing();
											}
										}
									});
								}
							});
				}
			}
		});

		chooser.showOpenDialog(null);
	}
}