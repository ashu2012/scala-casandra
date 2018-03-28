package com.cassandra.phantom.modeling

import com.cassandra.phantom.modeling.database.{Database, SongsDatabase}
import com.cassandra.phantom.modeling.entity.Song
import com.cassandra.phantom.modeling.database.{Database, SongsDatabase}
import com.cassandra.phantom.modeling.entity.Song
import com.datastax.driver.core.utils.UUIDs
import com.cassandra.phantom.modeling.connector
import com.cassandra.phantom.modeling.connector.Connector.config
import com.datastax.driver.core.SocketOptions
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import scala.util.{Try,Success,Failure}
import java.io.File

/**
  * Tests Songs methods against an embedded cassandra
  *
  * Before executing it will create all necessary tables in our embedded cassandra
  * validating our model with the requirements described in the readme.md file
  */



import java.net.InetAddress

import com.datastax.driver.core.Cluster
import com.typesafe.config.ConfigFactory
import com.outworkers.phantom.connectors.{KeySpace, SessionProvider}
import com.outworkers.phantom.dsl._
import collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Properties



class MyConfig(fileNameOption: Option[String] = None) {

  val config = fileNameOption.fold(
    ifEmpty = ConfigFactory.load() )(
    file => ConfigFactory.parseFile(new File(file) ))

  def envOrElseConfig(name: String): String = {
    Properties.envOrElse(
      name.toUpperCase.replaceAll("""\.""", "_"),
      config.getString(name)
    )
  }

  def getOrElse()={
    config
  }
}


object ConfigObj {
  val defaultConfig= ConfigFactory.load()
  var myConfigObj : Option[MyConfig]= None
  def getConfig= myConfigObj.get.config
  lazy val config = getConfig

}


object DataConnection {
  lazy val config = ConfigObj.config
  //val hosts: Seq[String] = Config.config.getStringList("cassandra.host").toList
  lazy private val hosts = config.getStringList("cassandra.host").asScala
  lazy val inets = hosts.map(InetAddress.getByName).toArray.toSeq
  lazy private val keySpace = config.getString("cassandra.keyspace")
  lazy private val userName = config.getString("cassandra.username")
  lazy private val password = config.getString("cassandra.password")
  lazy val port = config.getString("cassandra.port").toInt

  /**
  val cluster =
    Cluster.builder()
      .addContactPoints(inets)
      .withClusterName(Config.config.getString("cassandra.cluster"))
      //      .withCredentials(config.getString("cassandra.username"), config.getString("cassandra.password"))
      .build()


    * Create a connector with the ability to connects to multiple hosts in a cluster
    *
    * If you need to connect to a secure cluster, use:
    * {{{
    * ContactPoints(hosts)
    *   .withClusterBuilder(_.withCredentials(username, password))
    *   .keySpace(keyspace)
    * }}}
    *
    */
  lazy val connector1: CassandraConnection = ContactPoints(hosts,port).withClusterBuilder(_.withCredentials(userName, password)).keySpace(keySpace)

  lazy val connector2: CassandraConnection ={{{
     ContactPoints(hosts, port)
       .withClusterBuilder(_.withCredentials(userName, password))
       .keySpace(keySpace)
     }}}

  lazy val connector3 = Cluster.builder().addContactPoints(inets).withPort(port).withSSL().withCredentials(userName, password).withSocketOptions(
    new SocketOptions()
      .setConnectTimeoutMillis(10000)).build().connect()


}


object mainClass{
  def main(args: Array[String]): Unit = {

      if (args.length != 0) {
        println("reading application.conf file passed as argument")
        val obj = new MyConfig(Some(args(0)))
        ConfigObj.myConfigObj=Some(obj)

      }
    else{
        println("reading default config for cassandra")
        val obj = new MyConfig()
        ConfigObj.myConfigObj=Some(obj)
      }

    println("give input 1 ,2 3 for trying different connector:-" )
    val line = scala.io.StdIn.readLine().toInt

    line match  {
      case 1 =>{
        println("trying first connector ....................")
        Try(DataConnection.connector1.session.execute("DESCRIBE keyspaces;"))match {
          case Success(lines) => lines.foreach(println)
          case Failure(f) => println(f)
        }
      }

      case 2 => {
        println("trying second  connector .............")
        Try( DataConnection.connector2.session.execute("DESCRIBE keyspaces;")) match {
          case Success(lines) => lines.foreach(println)
          case Failure(f) => println(f)
        }
      }

      case 3 => {
        println("trying third connector ....................")
        Try(DataConnection.connector2.session.execute("DESCRIBE keyspaces;"))match {
          case Success(lines) => lines.foreach(println)
          case Failure(f) => println(f)
        }
      }
    }




  }
}