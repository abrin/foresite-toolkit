
import doctest
import unittest
import glob
import os

optionflags = (doctest.REPORT_ONLY_FIRST_FAILURE |
               doctest.NORMALIZE_WHITESPACE |
               doctest.ELLIPSIS)

def open_file(filename, mode='r'):
    """Helper function to open files from within the tests package."""
    return open(os.path.join(os.path.dirname(__file__), filename), mode)

def setUp(test):
    test.globs.update(dict(
        open_file = open_file,
        ))

def test_suite():
    return unittest.TestSuite([
        doctest.DocFileSuite(
        'README.txt',
        package='foresite',
        optionflags=optionflags,
        setUp=setUp
        )])

if __name__ == "__main__":
    runner = unittest.TextTestRunner(verbosity=1)
    runner.run(test_suite())
    
