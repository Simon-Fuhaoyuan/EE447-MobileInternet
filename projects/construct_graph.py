import numpy as np
from tqdm import tqdm


classes = {
    'Case_Based': 0,
    'Genetic_Algorithms': 1,
    'Neural_Networks': 2,
    'Probabilistic_Methods': 3,
    'Reinforcement_Learning': 4,
    'Rule_Learning': 5,
    'Theory': 6
}

papers = {}
# (str)'paper_id': ((int)idx, (int)class_idx)

def main():
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
            papers[paper_id] = (idx, classes[paper_class])
            n_papers += 1

    adj = np.zeros((n_papers, n_papers))
    with open(cites, 'r') as f:
        lines = f.readlines()
        for line in lines:
            citing = line.split('\t')
            cited_paper = papers[citing[0]][0]
            citing_paper = papers[citing[1][:-1]][0]
            adj[citing_paper][cited_paper] = 1

    out_degree = np.zeros((n_papers, n_papers))
    out_degree_norm = np.zeros((n_papers, n_papers))
    for i in range(n_papers):
        degree = adj[i].sum()
        if degree == 0:
            degree = 1
        out_degree[i][i] = degree
        out_degree_norm[i][i] = degree ** (-0.5)
    
    Laplace = np.dot(out_degree_norm, (out_degree - adj))
    Laplace = np.dot(Laplace, out_degree_norm)

    print('Calculating eigen vectors...')
    _, eig_vector = np.linalg.eig(Laplace)
    
    print('Calculating distances...')
    distance = np.zeros((n_papers, n_papers))
    for i in tqdm(range(n_papers)):
        for j in range(n_papers):
            distance[i][j] = np.linalg.norm((eig_vector[i] - eig_vector[j]))


if __name__ == "__main__":
    main()