package models

case class User(id:Long, username:String, password:String)

class UserRepository {
  def findUser(username: String, password: String): Boolean = {
    true
  }
}