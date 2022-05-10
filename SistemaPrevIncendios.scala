package programas

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import org.json.simple.JSONObject;
import SensorHumo.activeMqUrl

object SistemaPrevIncendios {

  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    val connection = cFactory.createConnection
    connection.start

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val hq = session.createQueue("HumoQueue")

    val consumidor = session.createConsumer(hq)

    val listener = new MessageListener {
      def onMessage(message: Message): Unit ={
        message match {
          case objeto: ObjectMessage => {
            val lectura : JSONObject = objeto.getObject()

            //Creacion olas de msg 
            val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            val rq = session.createQueue("RociadoresQueue")
            val aq = session.createQueue("AlarmaQueue")
            val bq = session.createQueue("BomberosQueue")
            
            //Se extrae el valor del objeto
            val nivelHumo : int =  lectura.getInt("nivel")
            println(s"Se recibio la lectura de : " + nivelHumo)
            val alerta: JSONObject = new JSONObject()
            

            //Procesamiento del valor
            if (nivelHumo > 100) {
              println("La lectura recibida supera el umbral seguro. Activando plan de emergencia.")
              alerta.put("emergencia", true)
            } else {
              alerta.put("emergencia", false)
            }

            //prod para rociadores
            val prodRoc = session.createProducer(rq)

            //prod para alarma
            val prodAla = session.createProducer(aq)

            //prod para Bomberos
            val prodBom = sesion.createProducer(bq)

            //hacer que cree un mensaje JSON
            val objMessage = session.createObjectMessage(alerta)
            prodRoc.send(objMessage)
            prodAla.send(objMessage)
            prodBom.send(objMessage)
            

            //Enviar datos a persistencia PENDIENTE
          }
          case _ => {
            throw new Exception("Error desconocido")
          }
        }
      }
    }
    consumidor.setMessageListener(listener)
  }
}