import pandas as pd
import numpy as np

tree = np.load('npy/tree_.npy')
size = tree.shape[0]


content = './cora/cora.content'
id = [i + 1 for i in range(size)]
classes = ['Ground_Node']
n_papers = 1
with open(content, 'r') as f:
    lines = f.readlines()
    for line in lines:
        attrs = line.split('\t')
        paper_id = attrs[0]
        idx = n_papers
        paper_class = attrs[-1][:-1] # delete '\n'
        classes.append(paper_class)
        n_papers += 1

point_frame = pd.DataFrame({'Id': id, 'Class': classes})
point_frame.to_csv('csv/tree_points.csv', index=False, sep='\t')

edge_type = 'directed'
types = []
source = []
target = []
for i in range(size):
    for j in range(size):
        if tree[i][j] > 0:
            types.append(edge_type)
            source.append(i+1)
            target.append(j+1)
id = [i + 1 for i in range(len(types))]
edge_frame = pd.DataFrame({'Id': id, 'Source': source, 'Target': target, 'Type': types})
edge_frame.to_csv('csv/tree_edges.csv', index=False, sep='\t')