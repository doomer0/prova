/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.AbstractCellPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class GroupByHeaderPainter extends AbstractCellPainter {

	private static final int LEFT_INDENT = 2;
	private static final int ENDCAP_WIDTH = 8;
	private static final int X_PADDING = 4;
	private static final int Y_PADDING = 2;
	
	private final GroupByModel groupByModel;
	private final IDataProvider columnHeaderDataProvider;
	
	private List<Rectangle> groupByCellBounds = new ArrayList<Rectangle>();

	public GroupByHeaderPainter(GroupByModel groupByModel, IDataProvider columnHeaderDataProvider) {
		this.groupByModel = groupByModel;
		this.columnHeaderDataProvider = columnHeaderDataProvider;
	}

	public void paintCell(LayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
		Color originalBackground = gc.getBackground();
		Color originalForeground = gc.getForeground();
		Font originalFont = gc.getFont();
		
		// Draw background
		gc.fillRectangle(bounds);
		
		groupByCellBounds.clear();
		
		List<Integer> groupByColumnIndexes = groupByModel.getGroupByColumnIndexes();
		if (groupByColumnIndexes.size() > 0) {
			IStyle cellStyle = CellStyleUtil.getCellStyle(cell, configRegistry);
			gc.setBackground(cellStyle.getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR));
			gc.setForeground(cellStyle.getAttributeValue(CellStyleAttributes.FOREGROUND_COLOR));
			gc.setFont(cellStyle.getAttributeValue(CellStyleAttributes.FONT));
			gc.setAntialias(GUIHelper.DEFAULT_ANTIALIAS);
			gc.setTextAntialias(GUIHelper.DEFAULT_TEXT_ANTIALIAS);
			
			int textHeight = gc.textExtent("X").y; //$NON-NLS-1$
			
			int x0 = bounds.x + LEFT_INDENT;
			int y0 = bounds.y + bounds.height/2 - textHeight/2 - Y_PADDING;
			int y_height = Y_PADDING + textHeight + Y_PADDING;
			
			// Draw leftmost edge
			gc.drawLine(x0, y0, x0, y0 + y_height);
			x0++;
			
			// Draw group by columns
			int lastColumnIndex = groupByColumnIndexes.size() - 1;
			for (int i = 0; i <= lastColumnIndex; i++) {
				int columnIndex = groupByColumnIndexes.get(i);
				
				String columnName = (String) columnHeaderDataProvider.getDataValue(columnIndex, 0);
				int textWidth = gc.textExtent(columnName).x;
				
				groupByCellBounds.add(new Rectangle(x0, y0, X_PADDING + textWidth + X_PADDING, y_height));
				
				gc.fillRectangle(x0, y0, X_PADDING + textWidth + X_PADDING, y_height);
				gc.drawLine(x0, y0, x0 + X_PADDING + textWidth + X_PADDING, y0);
				gc.drawLine(x0, y0 + y_height, x0 + X_PADDING + textWidth + X_PADDING, y0 + y_height);
				
				gc.drawText(columnName, x0 + X_PADDING, y0 + Y_PADDING);
				
				x0 += X_PADDING + textWidth + X_PADDING;
				
				// Draw end cap
				if (i < lastColumnIndex) {
					gc.fillRectangle(x0, y0, ENDCAP_WIDTH, y_height);
					gc.drawLine(x0, y0, x0 + ENDCAP_WIDTH, y0);
					gc.drawLine(x0, y0 + y_height, x0 + ENDCAP_WIDTH, y0 + y_height);
				} else {
					gc.fillPolygon(new int[] {
							x0, y0,
							x0 + ENDCAP_WIDTH, y0 + y_height/2,
							x0, y0 + y_height
					});
				}
				gc.drawLine(x0, y0, x0 + ENDCAP_WIDTH - 1, y0 + y_height/2);
				gc.drawLine(x0, y0 + y_height, x0 + ENDCAP_WIDTH - 1, y0 + y_height/2);
				
				x0 += ENDCAP_WIDTH;
			}
		}
		
		gc.setBackground(originalBackground);
		gc.setForeground(originalForeground);
		gc.setFont(originalFont);
	}

	public int getPreferredWidth(LayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return 0;
	}

	public int getPreferredHeight(LayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return getPreferredHeight();
	}

	public int getPreferredHeight() {
		return 30;
	}

	public int getGroupByColumnIndexAtXY(int x, int y) {
		for (int i = 0; i < groupByCellBounds.size(); i++) {
			Rectangle bounds = groupByCellBounds.get(i);
			if (bounds.contains(x, y)) {
				return groupByModel.getGroupByColumnIndexes().get(i);
			}
		}
		return -1;
	}
	
}