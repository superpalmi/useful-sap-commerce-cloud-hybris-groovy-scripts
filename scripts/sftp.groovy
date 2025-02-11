/**
 * This script is used to test the connection to an SFTP server.
 */

import org.springframework.integration.sftp.session.DefaultSftpSessionFactory

def session_factory = new DefaultSftpSessionFactory()
session_factory.setAllowUnknownKeys(true)
session_factory.setEnableDaemonThread(false)
session_factory.setPort(2230)
session_factory.setHost("myhost.com")
session_factory.setPassword('examplepassword')
session_factory.setUser("exampleuser")

// prints files inside folder to test connection
def session = session_factory.getSession()
try {

    def files = session.list("/myfolder")
    files.each { it ->
        println it
    }

    session.close()

} catch (Exception e) {
    println("Error: ${e.message} ${e.cause} ${e.stackTrace}")
    session.close()
}