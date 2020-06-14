import pandas as pd
import numpy as np

def produce_csv(papers, adj, directed=True):
    size = len(papers.keys())

    id = [i + 1 for i in range(size)]
    labels = []
    classes = []
    for label in papers.keys():
        labels.append(label)
        classes.append(papers[label][1])
    point_frame = pd.DataFrame({'Id': id, 'Label': labels, 'Class': classes})
    point_frame.to_csv('points.csv', index=False, sep='\t')

    edge_type = 'directed' if directed else 'undirected'
    types = []
    source = []
    target = []
    for i in range(size):
        for j in range(size):
            if adj[i][j] > 0:
                types.append(edge_type)
                source.append(i)
                target.append(j)
    id = [i + 1 for i in range(len(types))]
    edge_frame = pd.DataFrame({'Id': id, 'Source': source, 'Target': target, 'Type': types})
    edge_frame.to_csv('edges.csv', index=False, sep='\t')


if __name__ == "__main__":
    papers = {}
    content = './cora/cora.content'
    cites = './cora/cora.cites'
    
    n_papers = 0
    with open(content, 'r') as f:
        lines = f.readlines()
        for line in lines:
            attrs = line.split('\t')
            paper_id = attrs[0]
            idx = n_papers
            paper_class = attrs[-1][:-1] # delete '\n'
            papers[paper_id] = (idx, paper_class)
            n_papers += 1

    adj = np.zeros((n_papers, n_papers))
    with open(cites, 'r') as f:
        lines = f.readlines()
        for line in lines:
            citing = line.split('\t')
            cited_paper_idx = papers[citing[0]][0]
            citing_paper_idx = papers[citing[1][:-1]][0] # delete '\n'
            adj[citing_paper_idx][cited_paper_idx] = 1

    produce_csv(papers, adj)