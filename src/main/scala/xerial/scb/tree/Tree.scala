package xerial.scb.tree

//--------------------------------------
//
// Tree.scala
// Since: 2012/07/11 10:28
//
//--------------------------------------


abstract class Node[+A] {
  def isEmpty: Boolean

  def getOrElse[B >: A](alternative: => Node[B]): Node[B]
}

case object Empty extends Node[Nothing] {
  def isEmpty = true

  def getOrElse[B >: Nothing](alternative: => Node[B]): Node[B] = alternative
}

case class Tree[+A](elem: A, left: Node[A], right: Node[A]) extends Node[A] {
  def isEmpty = false

  def getOrElse[B >: A](alternative: => Node[B]): Node[B] = this
}

object BinaryTree {
  def empty[A] = new BinaryTree[A](Empty)
  def apply[A](root:A) = new BinaryTree[A](Tree(root, Empty, Empty))
}

class BinaryTree[A](val root: Node[A]) {

  override def toString = root.toString

  private class Finder(target:A, updater: Tree[A] => Node[A]) {
    // (newNode, found flag)
    def find(current: Node[A]): Option[Node[A]] = {
      current match {
        case Empty => None
        case t @ Tree(elem, left, right) =>
          if (elem == target)
            Some(updater(t))
          else {
            // search left tree
            find(left).map(Tree(elem, _, right)).orElse {
              // search right tree
              find(right).map(Tree(elem, left, _))
            }
          }
      }
    }
  }


  private def set(target:A, newChild:A, updater:Tree[A] => Tree[A]) : BinaryTree[A] = {
    val f = new Finder(target, updater)
    val newRoot = f.find(root)
    newRoot.map(r => new BinaryTree(r)).getOrElse(this)
  }
  
  // set left node
  def setLeft(target: A, newChild: A): BinaryTree[A] =
    set(target, newChild, { t => Tree(t.elem, Tree(newChild, Empty, Empty), t.right)})


  // set right node
  def setRight(target: A, newChild: A): BinaryTree[A] =
    set(target, newChild, { t => Tree(t.elem, t.left, Tree(newChild, Empty, Empty))})

}