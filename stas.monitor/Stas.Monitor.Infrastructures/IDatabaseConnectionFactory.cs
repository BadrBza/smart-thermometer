using System.Data;

namespace Stas.Monitor.Infrastructures
{
    public interface IDatabaseConnectionFactory
    {
        IDbConnection CreateConnection();
    }
}
