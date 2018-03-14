# Hackenbush


An application to play Red-Blue Hackenbush, with a collection of related classes.


## Getting Started

This was developed in the NetBeans IDE, in which the steps to get this running are as follows:
1. Create a new project using 'File->New Project...'
    1. Select 'Java' and 'Java Application' for the project type.
    1. Give the project an appropriate name and location.
1. Clone the repo to your computer.
1. Copy the contents of 'src' into '/YourProject/src/'.
1. Set 'YourProject' as the main project in NetBeans, and let 'my.hackenbushapplication.main' be the main class.
1. Run it with NetBeans 'Run' button. In the window, you can make whatever Hackenbush game you like. For further directions, see the next section.
Better and more general instructions will follow in a later update. Right now I'm just fighting with NetBeans.

## Using the GUI

The window contains two text fields, in which you can enter adjacency matrices for the red and blue sticks of a Hackenbush game. To see what this game actually looks like, press the 'Update Graph' button.

Adjacency matrices must be symmetric, the same size, and contain only non-negative integers. Edges not connected to the ground will be automatically deleted when the graph is updated.

This is clearly not the most intuitive way to play a Hackenbush game, and adjustments will be made in later updates.

## Combinatorial Game Values

For more information about how combinatorial game values are calculated, I would suggest http://www.geometer.org/mathcircles/hackenbush.pdf.

Right now, calculation of game values is only enabled when the graph in question is a tree.

## Acknowledgements

* This project began as an undergraduate thesis under the direction of Prof. Adam Landsberg at the Keck Science Department, who directed me to resources on combinatorial game values and suggested Red-Blue Hackenbush as an area of study.
* John Conway and a bunch of other people.