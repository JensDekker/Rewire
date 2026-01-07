# Figma Structure Analysis

## Component Hierarchy

### Group 1 (Main Container)

#### Green Plug (Left Brain - Dark Green #276631)
- **Green Plug - Prongs** (extending downward)
  - Rectangle 10
  - Rectangle 9
  - Rectangle 8
- **Green Plug - Body**
  - Rectangle 7
  - Rectangle 6
  - Rectangle 5

#### Blue Plug (Right Brain - Teal #278090)
- **Blue Plug - Prongs** (extending upward - these are the "slots")
  - Rectangle 10
  - Rectangle 9
  - Rectangle 8
- **Blue Plug - Body**
  - Rectangle 7
  - Rectangle 6
  - Rectangle 5

### Wire System (Circular Path - Light Green #7DBD4C)

#### Side Wire - Top
- Ellipse 4 (white node)
- Rectangle 4 (connecting line)
- Ellipse 3 (white node)
- Rectangle 3 (connecting line)

#### Side Wire - Bottom
- Ellipse 4 (white node)
- Rectangle 4 (connecting line)
- Ellipse 3 (white node)
- Rectangle 3 (connecting line)

#### Curved Wire - Top
- Rectangle 2 (arc segment)
- Rectangle 1 (arc segment)
- Ellipse 2 (white node)
- Ellipse 1 (white node)

#### Curved Wire - Bottom
- Rectangle 2 (arc segment)
- Rectangle 1 (arc segment)
- Ellipse 2 (white node)
- Ellipse 1 (white node)

## Key Insights

1. **The "Wires" are the circular path** - not a single continuous shape but composed of:
   - Curved segments (top and bottom arcs)
   - Side segments (vertical connectors)
   - Ellipses (white connection nodes)

2. **Each Plug has identical structure** - Prongs + Body, just different colors and orientations

3. **The structure suggests**:
   - Curved Wires create the arc portions
   - Side Wires create the vertical connection segments
   - Ellipses are the white nodes at connection points

## Implementation Strategy

1. **Start with Green Plug (Left Brain)**
   - Build Body first (Rectangles 5, 6, 7)
   - Then add Prongs (Rectangles 8, 9, 10)

2. **Then Blue Plug (Right Brain)**
   - Build Body first (Rectangles 5, 6, 7)
   - Then add Prongs/Slots (Rectangles 8, 9, 10)

3. **Then Wire System**
   - Curved Wire - Top (arc)
   - Curved Wire - Bottom (arc)
   - Side Wire - Top (vertical connector)
   - Side Wire - Bottom (vertical connector)
   - Add Ellipses (white nodes) at connection points

