# Maze Project AI  

![Python](https://img.shields.io/badge/Python-3.x-blue?logo=python)  
![Language](https://img.shields.io/badge/language-Java-yellow)  
![Algorithm](https://img.shields.io/badge/Algorithm-A*_Pathfinding-red)  
![ML](https://img.shields.io/badge/ML-Perceptron-green)  

---

## Overview  
This project implements a **Maze Generator and Solver** that combines:  
- **Random Maze Generation** (based on user-defined width & height).  
- **Perceptron Classifier** for tile safety classification.  
- **A\*** algorithm for finding the shortest **safe** path.  

The main objective is to navigate from a **start point** to an **end point** in the maze, while ensuring the path goes only through **safe tiles**.  

---

## Features  
- *Random Maze Generation** → Create a grid-based maze of any size.  
- **Custom Start & End Points** → Select specific locations for pathfinding.  
- **Edit Maze Mode** → Switch to edit mode and modify tiles manually:  
  - Add **Obstacles (walls)**  
  - Add **Grass (safe tile)**  
  - Add **Water (unsafe tile)**  
- **Perceptron Safety Classification** →  
  - Inputs: Tile type (Grass = 0, Water = 1), Elevation (0–10), Manhattan distance to nearest obstacle.  
  - Output:  
    - `1` → Safe tile  
    - `0` → Unsafe tile  
- **Pathfinding with A\*** →  
  - Finds the shortest safe path.  
  - Uses Manhattan Distance as heuristic.  
- Handles blocked cases → Displays **“No safe path found”** if no valid path exists.  

---

## Getting Started  

### Requirements
- Python 3.x  
- Libraries: `numpy`, `matplotlib`, `pygame` (if visualization is used)  

### Installation
```bash
# Clone repository
git clone https://github.com/Ali-Odeh/Maze_Project_AI.git
cd Maze_Project_AI

# Install dependencies
pip install -r requirements.txt

# Run the main application
python src/main.py

