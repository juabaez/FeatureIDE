/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.core.cnf;

import java.util.Comparator;
import java.util.List;

/**
 * Compares list of clauses by he number of literals.
 * 
 * @author Sebastian Krieter
 */
public class ClauseListLengthComparatorAsc implements Comparator<List<LiteralSet>> {

	@Override
	public int compare(List<LiteralSet> o1, List<LiteralSet> o2) {
		return addLengths(o1) - addLengths(o2);
	}

	protected int addLengths(List<LiteralSet> o) {
		int count = 0;
		for (LiteralSet literalSet : o) {
			count += literalSet.getLiterals().length;
		}
		return count;
	}

}