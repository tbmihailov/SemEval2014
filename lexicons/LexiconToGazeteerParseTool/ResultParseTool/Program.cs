using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace ResultParseTool
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length != 3)
            {
                Console.WriteLine("SemEval Task B input and result files combine");
                Console.WriteLine("");
                Console.WriteLine("3 params expected:");
                Console.WriteLine("1.input file 1");
                Console.WriteLine("2.input file 2");
                Console.WriteLine("3.output file");
            }

            
            string inputFile1 = args[0];
            string inputFile2 = args[1];
            string outputFile = args[2];

            string regexPattern1 = "(.+)\t(.+)\t(.+)\t(.+)";
            string regexReplace = "$1\t$2\t{0}\t$4";

            using (TextReader reader1 = new StreamReader(inputFile1))
            using (TextReader reader2 = new StreamReader(inputFile2))
            using (TextWriter writer = new StreamWriter(outputFile))
            {
                string inputLine1 = string.Empty;
                string inputLine2 = string.Empty;
                while (((inputLine1 = reader1.ReadLine()) != null)
                        && ((inputLine2 = reader2.ReadLine()) != null))
                {
                    string regExWithRes = string.Format(regexReplace, inputLine2);
                    string outputLine = Regex.Replace(inputLine1, regexPattern1, regExWithRes);
                    writer.WriteLine(outputLine);
                }
            }
        }
    }
}
