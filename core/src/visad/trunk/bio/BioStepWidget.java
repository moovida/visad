//
// BioStepWidget.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 2002 Bill Hibbard, Curtis Rueden, Tom
Rink, Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and
Tommy Jasmin.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA 02111-1307, USA
*/

package visad.bio;

import visad.util.StepWidget;

/**
 * BioStepWidget is a slider GUI component with
 * directional step arrows at either end, used by VisBio.
 */
public class BioStepWidget extends StepWidget {

  // -- FIELDS --

  /** VisBio frame. */
  protected VisBio bio;


  // -- CONSTRUCTOR --

  /** Constructs a BioStepWidget. */
  public BioStepWidget(VisBio biovis, boolean horizontal) {
    super(horizontal);
    bio = biovis;
  }


  // -- API METHODS --

  /** Updates slider bounds. */
  public void updateSlider(int maximum) {
    if (maximum > 0) {
      setEnabled(true);
      max = maximum;
    }
    else {
      setEnabled(false);
      max = 1;
    }
    cur = 1;
    setBounds(min, max, cur);
  }

}
