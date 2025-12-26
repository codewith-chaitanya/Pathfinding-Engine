const rows = 20, cols = 20;
let startNode = {x: 0, y: 0};
let endNode = {x: 19, y: 19};
let walls = new Set();

const gridElement = document.getElementById('grid');

// 1. Render Grid
function createGrid() {
    gridElement.innerHTML = '';
    for (let r = 0; r < rows; r++) {
        for (let c = 0; c < cols; c++) {
            const cell = document.createElement('div');
            cell.classList.add('cell');
            cell.dataset.r = r;
            cell.dataset.c = c;
            
            // Initial Colors
            if (r === startNode.x && c === startNode.y) cell.classList.add('start');
            else if (r === endNode.x && c === endNode.y) cell.classList.add('end');
            
            // Click Handler
            cell.addEventListener('click', (e) => {
                if (e.shiftKey) { // Move Start
                    startNode = {x: r, y: c};
                    createGrid();
                } else if (e.ctrlKey || e.metaKey) { // Move End
                    endNode = {x: r, y: c};
                    createGrid();
                } else { // Toggle Wall
                    const key = `${r}:${c}`;
                    if (walls.has(key)) walls.delete(key);
                    else walls.add(key);
                    cell.classList.toggle('wall');
                }
            });
            gridElement.appendChild(cell);
        }
    }
}

// 2. Call Java API
async function findPath() {
    // Format data for our custom Java parser: "StartX,StartY|EndX,EndY|Wall1,Wall2..."
    const wallStr = Array.from(walls).join(',');
    const payload = `${startNode.x},${startNode.y}|${endNode.x},${endNode.y}|${wallStr}`;

    try {
        const response = await fetch('http://localhost:8000/api/path', {
            method: 'POST',
            body: payload
        });
        
        const path = await response.json();
        animatePath(path);
    } catch (err) {
        console.error("Error talking to Java:", err);
        alert("Ensure Java Server is running!");
    }
}

function animatePath(path) {
    path.forEach((node, i) => {
        setTimeout(() => {
            const cell = document.querySelector(`.cell[data-r='${node.x}'][data-c='${node.y}']`);
            if (cell && !cell.classList.contains('start') && !cell.classList.contains('end')) {
                cell.classList.add('path');
            }
        }, 30 * i);
    });
}

createGrid();