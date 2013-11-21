/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.resize.command;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.command.AbstractDimPositionsCommand;
import org.eclipse.nebula.widgets.nattable.command.LayerCommandUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class MultiColumnResizeCommand extends AbstractDimPositionsCommand {

	private int commonColumnWidth = -1;
	protected Map<ColumnPositionCoordinate, Integer> colPositionToWidth = new HashMap<ColumnPositionCoordinate, Integer>();
	
	
	/**
	 * All columns are being resized to the same width e.g. during a drag resize
	 */
	public MultiColumnResizeCommand(ILayer layer, Collection<Range> columnPositions, int commonColumnWidth) {
		super(layer.getDim(HORIZONTAL), columnPositions);
		this.commonColumnWidth = commonColumnWidth;
	}
	
	/**
	 * Each column is being resized to a different size e.g. during auto resize
	 */
	public MultiColumnResizeCommand(ILayer layer, int[] columnPositions, int[] columnWidths) {
		super(layer.getDim(HORIZONTAL), new RangeList(columnPositions));
		for (int i = 0; i < columnPositions.length; i++) {
			colPositionToWidth.put(new ColumnPositionCoordinate(layer, columnPositions[i]), Integer.valueOf(columnWidths[i]));
		}
	}
	
	protected MultiColumnResizeCommand(MultiColumnResizeCommand command) {
		super(command);
		this.commonColumnWidth = command.commonColumnWidth;
		this.colPositionToWidth = new HashMap<ColumnPositionCoordinate, Integer>(command.colPositionToWidth);
	}
	
	@Override
	public MultiColumnResizeCommand cloneCommand() {
		return new MultiColumnResizeCommand(this);
	}
	
	
	public int getCommonColumnWidth() {
		return commonColumnWidth;
	}
	
	public int getColumnWidth(int columnPosition) {
		for (ColumnPositionCoordinate columnPositionCoordinate : colPositionToWidth.keySet()) {
			if (columnPositionCoordinate.getColumnPosition() == columnPosition) {
				return colPositionToWidth.get(columnPositionCoordinate).intValue();
			}
		}
		return commonColumnWidth;
	}
	
	@Override
	public boolean convertToTargetLayer(ILayer targetLayer) {
		// Ensure that the width associated with the column is now associated with the converted 
		// column position.
		Map<ColumnPositionCoordinate, Integer> newColPositionToWidth = new HashMap<ColumnPositionCoordinate, Integer>();
		
		for (ColumnPositionCoordinate columnPositionCoordinate : colPositionToWidth.keySet()) {
			ColumnPositionCoordinate convertedColumnPositionCoordinate = LayerCommandUtil.convertColumnPositionToTargetContext(columnPositionCoordinate, targetLayer);
			if (convertedColumnPositionCoordinate != null) {
				newColPositionToWidth.put(convertedColumnPositionCoordinate, colPositionToWidth.get(columnPositionCoordinate));
			}
		}
		
		if (super.convertToTargetLayer(targetLayer)) {
			colPositionToWidth = newColPositionToWidth;
			return true;
		} else {
			return false;
		}
	}
	
}
