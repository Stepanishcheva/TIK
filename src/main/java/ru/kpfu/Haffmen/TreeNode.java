package ru.kpfu.Haffmen;

public class TreeNode implements Comparable<TreeNode> {
    Character content;
    int weight;
    TreeNode left;
    TreeNode right;

    public TreeNode(Character content, int weight) {
        this.content = content;
        this.weight = weight;
    }

    public TreeNode(Character content, int weight, TreeNode left, TreeNode right) {
        this.content = content;
        this.weight = weight;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(TreeNode o) {
        return o.weight - weight;
    }


    public String getCharCode(Character ch, String parentPath) {
        if (content == ch) {
            return  parentPath;
        } else {
            if (left != null) {
                String path = left.getCharCode(ch, parentPath + 0);
                if (path != null) {
                    return path;
                }
            }
            if (right != null) {
                String path = right.getCharCode(ch, parentPath + 1);
                if (path != null) {
                    return path;
                }
            }
        }
        return null;
    }
}
